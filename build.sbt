import sbt.Keys.libraryDependencies
import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}
/* 2.  Variables Declaration */

val artifactoryUrl = sys.env.getOrElse("ARTIFACTORY_HOST", "artifacts.dream11.com")
val artifactoryUserName = sys.env.getOrElse("ARTIFACTORY_USERNAME", "dev")
val artifactoryPassword = sys.env.getOrElse("ARTIFACTORY_PASSWORD", "AP8bBPkHMedHHxqYSj5huF1ACoY")
val env = sys.env.getOrElse("ENV", "stag")

lazy val prioritisationService = project.in(file(".")).aggregate(igniteDb)

lazy val igniteDb = (project in file("igniteDb"))
    .enablePlugins(JavaAppPackaging, UniversalPlugin, DockerPlugin,Cinnamon, NewRelic)
  .settings(
    settings,
    libraryDependencies ++= commonDependencies ++ monitoringDependencies
  )
  .configs(Configs.all: _*)

lazy val prioritisationEngine = (project in file("prioritisationEngine"))
  .enablePlugins(JavaAppPackaging, UniversalPlugin, DockerPlugin,Cinnamon, NewRelic)
  .settings(
    settings,
    libraryDependencies ++= commonDependencies ++ monitoringDependencies
  )
  .configs(Configs.all: _*)

lazy val defaultSettings = Seq(
  organization := "com.dream11",
  organizationName := "Sporta Technologies",
  version := "0.1.0",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8"
  ),
  scalafmtOnCompile := true,
  resolvers += "Artifactory" at s"https://$artifactoryUrl/artifactory/d11-groups/",
  publishTo := Some(
    "Artifactory Realm" at s"https://$artifactoryUrl/artifactory/d11-groups"
  ),
  publishMavenStyle := true,
  javaOptions in Universal := Seq(
    "-Dcom.sun.management.jmxremote",
    "-Dcom.sun.management.jmxremote.port=1099", // port of the rmi registery
    "-Dcom.sun.management.jmxremote.rmi.port=1099", // port of the rmi server
    "-Dcom.sun.management.jmxremote.ssl=false", // To disable SSL
    "-Dcom.sun.management.jmxremote.local.only=false", // when true, it indicates that the local JMX RMI connector will only accept connection requests from local interfaces
    "-Dcom.sun.management.jmxremote.authenticate=false" // Password authentication for remote monitoring is disabled
  ),
  newrelicConfig := (resourceDirectory in Compile).value / "newrelic.yml",
  updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true),
  credentials += Credentials(
    "Artifactory Realm",
    artifactoryUrl,
    artifactoryUserName,
    artifactoryPassword
  ),
  cinnamon in run := true,
  cinnamon in test := false,
  cinnamonLogLevel := "ERROR",
  dockerCommands := Seq(
    Cmd("FROM", "openjdk:8u181-jre-alpine3.8 as builder"),
    Cmd("COPY", "target/docker/. /app/"),
    Cmd(
      "COPY",
      "target/scala-2.12/classes/logback-docker.xml /app/logback-docker.xml"
    ),
    Cmd("COPY", "target/scala-2.12/classes/newrelic.yml /app/newrelic.xml"),
    Cmd("FROM", "openjdk:8u181-jre-alpine3.8"),
    Cmd("RUN", "apk add --no-cache bash"),
    Cmd("RUN", "addgroup -S app && adduser -S app -s /bin/false -G app"),
    Cmd("COPY", "--chown=app:app", "--from=builder", "/app/. /app/"),
    Cmd("USER", "app"),
    Cmd("EXPOSE", "8558", "2442", "9000"),
    Cmd("WORKDIR", "/app"),
    ExecCmd(
      "CMD",
      "/bin/bash",
      s"""/app/stage/opt/docker/bin/${packageName.value}""",
      "-Dlogback.configurationFile=/app/logback-docker.xml",
      "-Dnewrelic.config.file=/app/newrelic.yml",
      s"""-Dnewrelic.environment=$env"""
    )
  )
)

lazy val settings = defaultSettings ++ Testing.settings

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value / "scalapb"
)

//This provides location of proto files to be generated.
PB.protoSources in Compile := Seq(new File("src/proto"))

// (optional) If you need scalapb/scalapb.proto or anything from
// google/protobuf/*.proto
// for more info on scala protobuf https://scalapb.github.io/docs/

/*3  Dependencies Initialization */

lazy val dependencies =
  new {
    val logbackV      = "1.2.3"
    val logstashV     = "4.11"
    val akkaV         = "2.5.22"
    val scalatestV    = "3.0.7"
    val akkaHttpV     = "10.1.1"
    val scalacheckV   = "1.14.0"
    val mockitoV      = "2.13.0"
    val scalaMockV    = "3.6.0"
    val sCoverageV    = "1.4.0-M3"
    val scalaLoggingV = "3.9.2"
    val inMemoryh2V   = "1.3.148"
    val inMemoryKafkaV = "2.3.0"
    val akkaStreamCommonV = "0.4.1"
    val akkaCommonV = "0.0.9"
    val akkaManagemntV = "1.0.5"
    val akkaSplitBrainResolverV = "1.1.12"
    val json4sV = "3.6.7"
    val jodaTimeV = "2.10.5"
    val ecsLoggingV = "0.1.3"
    val testEnv = "test"
    val kafkaV = "2.1.1"
    val profotbufV = "3.6.1"

    val scalatest      = "org.scalatest"             %% "scalatest"                   % scalatestV  % testEnv
    val scalacheck     = "org.scalacheck"            %% "scalacheck"                  % scalacheckV
    val mockito        = "org.mockito"               % "mockito-core"                 % mockitoV    % testEnv
    val scalaMock      = "org.scalamock"             %% "scalamock-scalatest-support" % scalaMockV  % testEnv
    val sCoverage      = "org.scoverage"             %% "scalac-scoverage-runtime"    % sCoverageV  % testEnv
    val scalatic       =  "org.scalactic"            %% "scalactic"                   % scalatestV
    val logback        =  "ch.qos.logback"             % "logback-classic"             % logbackV
    val logstash       =  "net.logstash.logback"       % "logstash-logback-encoder"    % logstashV
    val scalaLogging   =  "com.typesafe.scala-logging" %% "scala-logging"              % scalaLoggingV
    val inMemoryh2     =  "com.h2database"            % "h2"                          % inMemoryh2V % testEnv
    val inMemoryKafka  = "io.github.embeddedkafka"    %% "embedded-kafka"              % inMemoryKafkaV % testEnv
    val akkaTest       = "com.typesafe.akka"         %% "akka-testkit"                % akkaV       % testEnv
    val akkaHttpTest   = "com.typesafe.akka"         %% "akka-http-testkit"           % akkaHttpV   % testEnv
    val akkaStreamCommon = "com.dream11" %% "akka-stream-common" % akkaStreamCommonV
    val akkaMultiNodeTest = "com.typesafe.akka"      %% "akka-multi-node-testkit"     % akkaV
    val akkaCommon = "com.dream11" %% "akka-common" % akkaCommonV
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaV
    val akkaRemote ="com.typesafe.akka" %% "akka-remote" % akkaV
    val akkaCluster ="com.typesafe.akka" %% "akka-cluster" % akkaV
    val akkaClusterTools ="com.typesafe.akka" %% "akka-cluster-tools" % akkaV
    val akkaManagement   ="com.lightbend.akka.management" %% "akka-management" % akkaManagemntV
    val akkaManagementCluser ="com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagemntV
    val akkaManagementHttp ="com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagemntV
    val akkaClusterSharding ="com.typesafe.akka" %% "akka-cluster-sharding" % akkaV
    val akkaSplitBrainResolver ="com.lightbend.akka" %% "akka-split-brain-resolver" % akkaSplitBrainResolverV
    val json4s = "org.json4s" %% "json4s-ext" % json4sV
    val jodaTime = "joda-time" % "joda-time" % jodaTimeV
    val ecsLogging = "co.elastic.logging" % "ecs-logging-core" % ecsLoggingV
    val ecsLoggingEncoder = "co.elastic.logging" % "logback-ecs-encoder" % ecsLoggingV
    val kafka = "org.apache.kafka" %% "kafka" % kafkaV
    val profotbuf = "com.google.protobuf" % "protobuf-java" % profotbufV
    val ignite = "org.apache.ignite" % "ignite-core" % "2.9.0"
    val scalaNlpBreeze = "org.scalanlp" %% "breeze" % "1.0"
    val scalaNlpBreezeNative = "org.scalanlp" %% "breeze-natives" % "1.0"
    val scalaNlpBreezeViz = "org.scalanlp" %% "breeze-viz" % "1.0"
    val lombok = "org.projectlombok" % "lombok" % "1.16.16"
    val googleGuice = "com.google.inject" % "guice" % "3.0"
    val dream11Common = "com.dream11" % "common" % "2.0.5"
  }

val monitoringDependencies = Seq(
  Cinnamon.library.cinnamonCHMetrics,
  // Use Akka instrumentation
  Cinnamon.library.cinnamonAkka,
  Cinnamon.library.cinnamonAkkaStream,
  // Use Akka HTTP instrumentation
  Cinnamon.library.cinnamonAkkaHttp,
  //new relic
  Cinnamon.library.cinnamonNewRelic,
)

val commonDependencies = Seq(
  dependencies.scalatest,
  dependencies.scalacheck,
  dependencies.mockito,
  dependencies.scalaMock,
  dependencies.sCoverage,
  dependencies.scalatic,
  dependencies.logback,
  dependencies.logstash,
  dependencies.mockito,
  dependencies.scalaLogging,
  dependencies.inMemoryh2,
  dependencies.inMemoryKafka,
  dependencies.json4s,
  dependencies.jodaTime,
  dependencies.ecsLogging,
  dependencies.ecsLoggingEncoder,
  dependencies.kafka,
  dependencies.profotbuf,
  dependencies.ignite,
  dependencies.scalaNlpBreeze,
  dependencies.scalaNlpBreezeNative,
  dependencies.scalaNlpBreezeViz,
  dependencies.lombok,
  dependencies.googleGuice,
  dependencies.dream11Common
)

val akkaDependencies = Seq(
  dependencies.akkaTest,
  dependencies.akkaHttpTest,
  dependencies.akkaStreamCommon,
  dependencies.akkaMultiNodeTest,
  dependencies.akkaCommon,
  dependencies.akkaActor,
  dependencies.akkaRemote,
  dependencies.akkaCluster,
  dependencies.akkaClusterTools,
  dependencies.akkaManagement,
  dependencies.akkaManagementCluser,
  dependencies.akkaManagementHttp,
  dependencies.akkaClusterSharding,
  dependencies.akkaSplitBrainResolver
)

libraryDependencies ++= Seq(
  "org.apache.ignite" % "ignite-core" % "2.9.0",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "org.scalanlp" %% "breeze" % "1.0",
  "org.scalanlp" %% "breeze-natives" % "1.0",
  "org.scalanlp" %% "breeze-viz" % "1.0",
  "org.projectlombok" % "lombok" % "1.16.16"
)