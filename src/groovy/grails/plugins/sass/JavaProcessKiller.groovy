package grails.plugins.sass

import java.util.regex.Pattern

class JavaProcessKiller {

    void killAll(String processPattern) {
        getRunningJavaProcesses().each { String processLine ->
            if (processLine.contains(processPattern)) {
                String pidToKill = getPidFromProcessLine(processLine)
                killPid(pidToKill)
            }
        }
    }

    void killAllRegex(Pattern processRegexPattern) {
        getRunningJavaProcesses().each { String processLine ->
            if (processRegexPattern.matcher(processLine).matches()) {
                String pidToKill = getPidFromProcessLine(processLine)
                killPid(pidToKill)
            }
        }
    }

    protected String[] getRunningJavaProcesses() {
        def output = new ByteArrayOutputStream()
        def p = ['jps', '-lm'].execute()
        p.consumeProcessOutput(new PrintStream(output), System.err)
        p.waitFor()
        
        return output.toString().split("\\n")
    }

    protected String getPidFromProcessLine(String line) {
        def pidPattern = /^(\d+).*$/
        def matcher = (line =~ pidPattern)

        return matcher[0][1]
    }

    protected void killPid(String pid) {
        def killCommands = [
            ['taskkill', '/F', '/PID', pid],
            ['kill', pid]
        ]

        boolean processKilledSuccessfully = false
        killCommands.each { command ->
            if (!processKilledSuccessfully) {
                try {
                    def process = command.execute()
                    process.consumeProcessOutput()
                    process.waitFor()
                    processKilledSuccessfully = true
                }
                catch (ignored) {
                }
            }
        }
    }
}
