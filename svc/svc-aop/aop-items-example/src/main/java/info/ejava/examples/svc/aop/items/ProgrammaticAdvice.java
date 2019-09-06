package info.ejava.examples.svc.aop.items;

import info.ejava.examples.svc.aop.items.aspects.SampleAdvisor1;
import info.ejava.examples.svc.aop.items.dto.MowerDTO;
import info.ejava.examples.svc.aop.items.services.ItemsService;
import info.ejava.examples.svc.aop.items.services.MowersServiceImpl;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.CommandLineRunner;

//@Component
public class ProgrammaticAdvice implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        MowerDTO mower1 = MowerDTO.mowerBuilder().name("John Deer").build();
        MowerDTO mower2 = MowerDTO.mowerBuilder().name("Husqvarna").build();

        ItemsService<MowerDTO> mowerService = new MowersServiceImpl();
        mowerService.createItem(mower1);

        SampleAdvisor1 advice1 = new SampleAdvisor1();
        ProxyFactory proxyFactory = new ProxyFactory(mowerService);
        proxyFactory.addAdvice(advice1);
        ItemsService<MowerDTO> proxiedMowerService = (ItemsService<MowerDTO>) proxyFactory.getProxy();
        proxiedMowerService.createItem(mower2);
    }
}
