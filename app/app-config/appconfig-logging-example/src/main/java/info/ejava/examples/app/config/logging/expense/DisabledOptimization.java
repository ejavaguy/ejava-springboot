package info.ejava.examples.app.config.logging.expense;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DisabledOptimization implements CommandLineRunner {
    private static final boolean DEBUG_ENABLED = log.isDebugEnabled();

    @Override
    public void run(String... args) throws Exception {
        long[] ms = new long[5];
        int i=0;

        log.info("warmup logger");
        log.debug("warmup logger");
        ExpensiveToLog obj=new ExpensiveToLog();

        ms[i++]=System.currentTimeMillis();
        log.debug("debug for expensiveToLog: " + obj + "!");
        ms[i++]=System.currentTimeMillis();

        if (log.isDebugEnabled()) {
            log.debug("debug for expensiveToLog: " + obj +"!");
        }
        ms[i++]=System.currentTimeMillis();

        if (DEBUG_ENABLED) {
            log.debug("debug for expensiveToLog: " + obj + "!");
        }
        ms[i++]=System.currentTimeMillis();

        log.debug("debug for expensiveToLog: {}!", obj);
        ms[i]=System.currentTimeMillis();

        log.info("concat: {}, ifDebug={}, DEBUG_ENABLED={}, param={}",
                ms[1]-ms[0],
                ms[2]-ms[1],
                ms[3]-ms[2],
                ms[4]-ms[3]);
    }

    private class ExpensiveToLog {
        public String toString() {
            try { Thread.sleep(1000); } catch (Exception ex) {/*ignored*/}
            return "hello";
        }
    }
}

