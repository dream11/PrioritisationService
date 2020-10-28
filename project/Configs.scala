import sbt.config
import com.typesafe.sbt._

object Configs {
  val IntegrationTest = config("it") extend (sbt.Test)
  val Test = sbt.Test
  val MultiJvm = SbtMultiJvm.MultiJvmKeys.MultiJvm
  val all = Seq(Test, IntegrationTest, MultiJvm)
}