package info.ejava.examples.svc.aop.items;

import info.ejava.examples.svc.aop.items.dto.BedDTO;
import info.ejava.examples.svc.aop.items.dto.ItemDTO;
import info.ejava.examples.svc.aop.items.services.ItemsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReflectionAdvice implements CommandLineRunner {
    private final ItemsService<BedDTO> bedsService;

    @Override
    public void run(String... cmdArgs) throws Exception {
        //obtain reference to method using name and argument types
        Method method = ItemsService.class.getMethod("createItem", ItemDTO.class);
        log.info("method: {}", method);

        //invoke method using target object and args
        Object[] args = new Object[] { BedDTO.bedBuilder().name("Bunk Bed").build() };
        log.info("invoke calling: {}({})", method.getName(), args);
        Object result = method.invoke(bedsService, args);
        log.info("invoke {} returned: {}", method.getName(), result);

        //obtain result from invoke() return
        BedDTO createdBed = (BedDTO) result;
        log.info("created bed: {}", createdBed);
    }
}
