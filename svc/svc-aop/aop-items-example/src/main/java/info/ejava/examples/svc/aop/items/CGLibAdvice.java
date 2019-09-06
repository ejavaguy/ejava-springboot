package info.ejava.examples.svc.aop.items;

import info.ejava.examples.svc.aop.items.aspects.MyMethodInterceptor;
import info.ejava.examples.svc.aop.items.dto.ChairDTO;
import info.ejava.examples.svc.aop.items.services.ChairsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CGLibAdvice implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ChairsServiceImpl.class);
        enhancer.setCallback(new MyMethodInterceptor());
        ChairsServiceImpl chairsServiceProxy = (ChairsServiceImpl)enhancer.create();

        log.info("created proxy: {}", chairsServiceProxy.getClass());
        log.info("proxy implements interfaces: {}",
                ClassUtils.getAllInterfaces(chairsServiceProxy.getClass()));

        ChairDTO createdChair = chairsServiceProxy.createItem(
                ChairDTO.chairBuilder().name("Recliner").build());
        log.info("created chair: {}", createdChair);
    }
}
