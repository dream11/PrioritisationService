

name := "PrioritisationService"

version := "0.1"

scalaVersion := "2.13.3"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value / "scalapb"
)

//This provides location of proto files to be generated.
PB.protoSources in Compile := Seq(new File("src/proto"))

// (optional) If you need scalapb/scalapb.proto or anything from
// google/protobuf/*.proto
// for more info on scala protobuf https://scalapb.github.io/docs/

libraryDependencies ++= Seq(
  "org.apache.ignite" % "ignite-core" % "2.6.0",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "org.scalanlp" %% "breeze" % "1.0",
  "org.scalanlp" %% "breeze-natives" % "1.0",
  "org.scalanlp" %% "breeze-viz" % "1.0",
  "org.projectlombok" % "lombok" % "1.16.16"
)