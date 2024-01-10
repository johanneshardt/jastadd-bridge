rootProject.name = "jastadd-bridge"
include("server")
include("interop")
include("CalcRAG")
project(":CalcRAG").projectDir = file("examples/CalcRAG")