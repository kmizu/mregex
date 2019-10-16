organization := "com.github.kmizu"

name := "mregex"

def Scala211 = "2.11.12"
def Scala212 = "2.12.10"
def Scala213 = "2.13.1"

scalaVersion := Scala213

crossScalaVersions := Seq(Scala211, Scala212, Scala213)

libraryDependencies ++= List(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

val scaladocBranch = settingKey[String]("branch name for scaladoc -doc-source-url")

scaladocBranch := "master"

scalacOptions in (Compile, doc) ++= { Seq(
  "-sourcepath", baseDirectory.value.getAbsolutePath,
  "-doc-source-url", s"https://github.com/kmizu/mregex/tree/${scaladocBranch.value}€{FILE_PATH}.scala"
)}

testOptions in Test += Tests.Argument("-u", "target/scalatest-reports")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")

initialCommands in console += {
  Iterator("com.github.kmizu.mregex._").map("import "+).mkString("\n")
}

pomExtra := (
  <url>https://github.com/kmizu/mregex</url>
    <licenses>
      <license>
        <name>The MIT License</name>
        <url>http://www.opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:kmizu/mregex.git</url>
      <connection>scm:git:git@github.com:kmizu/mregex.git</connection>
    </scm>
    <developers>
      <developer>
        <id>kmizu</id>
        <name>Kota Mizushima</name>
        <url>https://github.com/kmizu</url>
      </developer>
    </developers>
  )

publishTo := sonatypePublishToBundle.value

credentials ++= {
  val sonatype = ("Sonatype Nexus Repository Manager", "oss.sonatype.org")
  def loadMavenCredentials(file: java.io.File) : Seq[Credentials] = {
    xml.XML.loadFile(file) \ "servers" \ "server" map (s => {
      val host = (s \ "id").text
      val realm = if (host == sonatype._2) sonatype._1 else "Unknown"
      Credentials(realm, host, (s \ "username").text, (s \ "password").text)
    })
  }
  val ivyCredentials   = Path.userHome / ".ivy2" / ".credentials"
  val mavenCredentials = Path.userHome / ".m2"   / "settings.xml"
  (ivyCredentials.asFile, mavenCredentials.asFile) match {
    case (ivy, _) if ivy.canRead => Credentials(ivy) :: Nil
    case (_, mvn) if mvn.canRead => loadMavenCredentials(mvn)
    case _ => Nil
  }
}
