import sbt.{Credentials, Resolver}

val artifactoryUrl =
  sys.env.getOrElse("ARTIFACTORY_HOST", "artifacts.dream11.com")
val artifactoryUserName = sys.env.getOrElse("ARTIFACTORY_USERNAME", "dev")
val artifactoryPassword =
  sys.env.getOrElse("ARTIFACTORY_PASSWORD", "AP8bBPkHMedHHxqYSj5huF1ACoY")

credentials += Credentials(
  "Artifactory Realm",
  artifactoryUrl,
  artifactoryUserName,
  artifactoryPassword
)
sbtResolver := Resolver.url(
  "Artifactory",
  url(s"https://$artifactoryUrl/artifactory/d11-groups/")
)(Resolver.ivyStylePatterns)
// adds the task `dependencyUpdates` which shows a list of project dependencies that can be updated
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.4.1")

// adds several tasks that show the dependency tree. One of them is `dependencyBrowseGraph`, which opens a browser window with a visualization of the dependency graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

// adds the tasks `re-start`, `re-stop`, and  `re-status`
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")

//addSbtPlugin("com.dwijnand" % "sbt-dynver" % "3.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.21")

addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.13.1")

addSbtPlugin("com.gilt.sbt" % "sbt-newrelic" % "0.3.3")