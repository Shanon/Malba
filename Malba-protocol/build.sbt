import malba.{ Dependencies, MalbaBuild, Publish }

libraryDependencies ++= Dependencies.protocol


MalbaBuild.buildSettings

Publish.settings