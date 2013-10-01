package grails.plugins.sass

class JavaProcessKillerTest extends GroovyTestCase {
    
    private JavaProcessKiller javaProcessKiller = new JavaProcessKiller()

    void test_get_pid_from_process_line() {
        String pid = "1234"

        assertEquals("pid parsing does not grab pid correctly", pid, javaProcessKiller.getPidFromProcessLine("$pid org/jruby/Main -S compass"))
    }

    void test_getRunningJavaProcesses_returns_more_than_just_jps() {
        String[] processes = javaProcessKiller.getRunningJavaProcesses()

        assertNotNull(processes)
        assertFalse("No proceses found", 0 == processes.size())
        assertFalse("Only JPS process found", 1 == processes.size())
    }

    void test_killAll_successfully_kills_processes() {
        def oldProcessCount = javaProcessKiller.getRunningJavaProcesses().size()
        startAsynchronousJrubyProcess()
        javaProcessKiller.killAll("loop.rb")
        def newProcessCount = javaProcessKiller.getRunningJavaProcesses().size()

        assertEquals("Kill all is not properly killing processes", oldProcessCount, newProcessCount)
    }

    void test_killAllRegex_successfully_kills_processes() {
        def oldProcessCount = javaProcessKiller.getRunningJavaProcesses().size()
        startAsynchronousJrubyProcess()
        javaProcessKiller.killAllRegex(~/.*lo{2,3}p.[rR]b.*/)
        def newProcessCount = javaProcessKiller.getRunningJavaProcesses().size()

        assertEquals("Kill all is not properly killing processes", oldProcessCount, newProcessCount)
    }

    void startAsynchronousJrubyProcess() {
        Thread.start {
            def p = ['jruby', 'test/files/loop.rb'].execute()
            p.consumeProcessOutput(System.out, System.err)
            p.waitFor()
        }
    }
}
