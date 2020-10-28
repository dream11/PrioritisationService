import com.typesafe.sbt._
import sbt.Keys._
import sbt._

object Testing {

  lazy val itSettings =
    inConfig(IntegrationTest)(Defaults.itSettings) ++
      Seq(
        fork in IntegrationTest := false,
        parallelExecution in IntegrationTest := false,
        scalaSource in IntegrationTest := baseDirectory.value / "src/it/scala",
        resourceDirectory in IntegrationTest := baseDirectory.value / "src/it/resources"
      )

  lazy val testSettings = inConfig(Test)(Defaults.testSettings) ++ Seq(
    fork in Test := true,
    parallelExecution in Test := true
  )
  lazy val multiJvmSettings = inConfig(SbtMultiJvm.MultiJvmKeys.MultiJvm)(
    MultiJvmPlugin.multiJvmSettings
  ) ++ Seq(
    fork in SbtMultiJvm.MultiJvmKeys.MultiJvm := false,
    parallelExecution in SbtMultiJvm.MultiJvmKeys.MultiJvm := false,
    scalaSource in SbtMultiJvm.MultiJvmKeys.MultiJvm := baseDirectory.value / "src/multi-jvm/scala",
    resourceDirectory in SbtMultiJvm.MultiJvmKeys.MultiJvm := baseDirectory.value / "src/multi-jvm/resources"
  )

  lazy val settings = testSettings ++ itSettings ++ multiJvmSettings
}