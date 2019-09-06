package info.ejava.examples.svc.aop.items.aspects;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.svc.aop.items.dto.BedDTO;
import info.ejava.examples.svc.aop.items.dto.GrillDTO;
import info.ejava.examples.svc.aop.items.dto.MowerDTO;
import info.ejava.examples.svc.aop.items.services.ItemsService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ItemsAspect {

    //execution(<access> <return type> <package>.<class>.<method>(params))
    @Pointcut("execution(* info.ejava.examples.svc.aop.items.services.*.*(..))") //expression
    public void serviceMethod() { /*signature*/ }

    @Before("serviceMethod()")
    public void beforeServiceMethod(JoinPoint jp) {
        log.info("beforeServiceMethod: {}", jp);
    }

    //============================

    @Pointcut("args(info.ejava.examples.svc.aop.items.dto.GrillDTO)") //expression
    public void grillArgs() { /*signature*/ }

    @Pointcut("serviceMethod() && grillArgs()") //expression
    public void serviceMethodWithGrillArgs() { /*signature*/ }

    @Before("serviceMethodWithGrillArgs()")
    public void beforeGrillServiceMethod(JoinPoint jp) {
        log.info("beforeGrillServiceMethod: {}", jp);
    }

    @Before("serviceMethodWithGrillArgs()")
    public void beforeGrillServiceMethod() {
        log.info("beforeGrillServiceMethod");
    }
    //============================

    @Pointcut("execution(* info.ejava.examples.svc.aop.items..*.*(..))") //expression
    public void belowPackage() { /*signature*/ }

    @Before("belowPackage()")
    public void belowPackage(JoinPoint jp) {
        log.info("belowPackage: {}", jp);
    }
    //============================ return pattern

    @Pointcut("execution(*..GrillDTO *(..))") //expression
    public void returnsGrillPattern() { /*signature*/ }

    @Pointcut("execution(info.ejava.examples.svc.aop.items.dto.GrillDTO *(..))") //expression
    public void returnsFullyQualifiedGrill() { /*signature*/ }

    @Before("returnsGrillPattern() && returnsFullyQualifiedGrill()")
    public void returnsGrill(JoinPoint jp) {
        log.info("returnsGrill: {}", jp);
    }

    //============================ class pattern
    @Pointcut("execution(public * info.ejava.examples.svc.aop.items.services.GrillsServiceImpl.*(..))") //expression
    public void anyFullyQualifiedGrillServiceMethod() { /*signature*/ }

    @Pointcut("execution(* *..GrillsServiceImpl.*(..))") //expression
    public void anyPatternGrillServiceMethod() { /*signature*/ }

    @Pointcut("execution(* info.ejava.examples.svc..Grills*.*(..))") //expression
    public void grillServicePatternAnyMethod() { /*signature*/ }

    @Before("anyFullyQualifiedGrillServiceMethod() && anyPatternGrillServiceMethod() && grillServicePatternAnyMethod()")
    public void grillServiceMethod(JoinPoint jp) {
        log.info("grillServiceMethod: {}", jp);
    }

    //============================ method pattern

    @Pointcut("execution(* createItem(..))") //expression
    public void anyCreateItem() { /*signature*/ }

    @Pointcut("execution(* *..GrillsServiceImpl.createItem(..))") //expression
    public void grillServiceCreateItem() { /*signature*/ }

    @Pointcut("execution(* create*(..)))") //expression
    public void anyCreate() { /*signature*/ }

    @Before("anyCreateItem() && grillServiceCreateItem() && anyCreate()")
    public void createItemMethod(JoinPoint jp) {
        log.info("createItemMethod: {}", jp);
    }

    //============================ no arguments

    @Pointcut("execution(void info.ejava.examples.svc.aop.items.services.GrillsServiceImpl.deleteItems())") //expression
    public void explicitDeleteAllGrills() { /*signature*/ }

    @Pointcut("execution(* *..GrillsServiceImpl.*())")
    public void noargsGrillMethod() { /*signature*/ }

    @Pointcut("execution(* *..GrillsServiceImpl.delete*())")
    public void grillNoargsDeleteMethod() { /*signature*/ }

    @Before("explicitDeleteAllGrills() && noargsGrillMethod() && grillNoargsDeleteMethod()")
    public void grillDeleteAll(JoinPoint jp) {
        log.info("grillDeleteAll: {}", jp);
    }

    //============================ one argument

    @Pointcut("execution(* info.ejava.examples.svc.aop.items.services.GrillsServiceImpl.createItem(*))") //expression
    public void explicitCreateGrillOneParam() { /*signature*/ }

    @Pointcut("execution(* createItem(info.ejava.examples.svc.aop.items.dto.GrillDTO))") //expression
    public void createItemTakingExplicitGrillParam() { /*signature*/ }

    @Pointcut("execution(* *(*..GrillDTO))") //expression
    public void anyMethodTakingGrillParam() { /*signature*/ }

    @Before("explicitCreateGrillOneParam() && createItemTakingExplicitGrillParam() && anyMethodTakingGrillParam()")
    public void oneArgGrill(JoinPoint jp) {
        log.info("oneArgGrill: {}", jp);
    }

    //============================ multiple args

    @Pointcut("execution(* info.ejava.examples.svc.aop.items.services.GrillsServiceImpl.updateItem(*,*))")
    public void updateItemTwoParams() { /*signature*/ }

    @Pointcut("execution(* updateItem(int,*))")
    public void updateItemIntWildcard() { /*signature*/ }

    @Pointcut("execution(* updateItem(int,*..GrillDTO))")
    public void updateItemIntGrillDTO() { /*signature*/ }

    @Before("updateItemTwoParams() && updateItemIntWildcard() && updateItemIntGrillDTO()")
    public void updateItem(JoinPoint jp) {
        log.info("updateItem: {}", jp);
    }


    //============================ within

    @Pointcut("within(info.ejava.examples.svc.aop.items..*)")
    public void withinItemsSubpackage() { /*signature*/ }

    @Pointcut("within(*..ItemsService+)")
    public void withinImplementsItemsService() { /*signature*/ }

    @Pointcut("within(*..BedsServiceImpl)")
    public void withinBedsService() { /*signature*/ }

    @Pointcut("target(info.ejava.examples.svc.aop.items.services.BedsServiceImpl)")
    public void targetBedsServiceImpl() { /*signature*/ }

    @Pointcut("this(info.ejava.examples.svc.aop.items.services.BedsServiceImpl)")
    public void thisBedsServiceImpl() { /*signature*/ }

    @Before("withinBedsService() && withinImplementsItemsService() && withinItemsSubpackage() " +
            "&& targetBedsServiceImpl() && thisBedsServiceImpl()")
    public void beforeBeds(JoinPoint jp) {
        log.info("beforeBeds: {}", jp);
    }

    //============================ @target

    @Pointcut("@target(org.springframework.stereotype.Service)")
    public void targetServices() { /*signature*/ }

    @Pointcut("@annotation(order)")
    public void orderAnnotationValue(Order order) { /*signature*/ }

    @Pointcut("@annotation(org.springframework.core.annotation.Order)")
    public void orderAnnotation() { /*signature*/ }

    @Before("withinItemsSubpackage() && targetServices() && orderAnnotation() && orderAnnotationValue(order)")
    public void beforeAtTarget(JoinPoint jp, Order order) {
        log.info("before@Target@Annotation: {}, order {}", jp, order.value());
    }

    @Before("within(info.ejava.examples.svc.aop.items..*) && orderAnnotationValue(order)")
    public void beforeOrderAnnotation(Order order) {
        log.info("before@OrderAnnotation: order={}", order.value());
    }

    @Before("target(target) && this(proxy)")
    public void beforeTarget(ItemsService<BedDTO> target, Object proxy) {
        log.info("beforeTarget: target={}, proxy={}", target.getClass(), proxy.getClass());
    }

    //============================ parameter access

    @Pointcut("execution(* createItem(..)) && args(grillDTO)") //expression
    public void createGrill(GrillDTO grillDTO) { /*signature*/ }

    @Before("createGrill(grill)")
    public void beforeCreateGrillAdvice(GrillDTO grill) {
        log.info("beforeCreateGrillAdvice: {}", grill);
    }

    @Pointcut("execution(* updateItem(..)) && args(grillId, updatedGrill)") //expression
    public void updateGrill(int grillId, GrillDTO updatedGrill) {} //signature

    @Before("updateGrill(id, grill)")
    public void beforeUpdateGrillAdvice(int id, GrillDTO grill) {
        log.info("beforeUpdateGrillAdvice: {}, {}", id, grill);
    }

    @Before("execution(* *..Grills*.*(..))")
    public void beforeGrillsMethodsUnknown(JoinPoint jp) {
        log.info("beforeGrillsMethodsUnknown: {}.{}, {}",
                jp.getTarget().getClass().getSimpleName(),
                jp.getSignature().getName(),
                jp.getArgs());
    }

    //============================ advice types

    @Pointcut("execution(* *..MowersServiceImpl.updateItem(*,*)) && args(id, mowerUpdate)")
    public void mowerUpdate(int id, MowerDTO mowerUpdate) { /*signature*/ }

    @Before("mowerUpdate(id, mowerUpdate)")
    public void beforeMowerUpdate(JoinPoint jp, int id, MowerDTO mowerUpdate) {
        log.info("beforeMowerUpdate: {}, {}", id, mowerUpdate);
    }

    @AfterReturning(value = "mowerUpdate(id, mowerUpdate)",
        returning = "result")
    public void afterReturningMowerUpdate(JoinPoint jp, int id, MowerDTO mowerUpdate, MowerDTO result) {
        log.info("afterReturningMowerUpdate: {}, {} => {}", id, mowerUpdate, result);
    }

    @AfterThrowing(value = "mowerUpdate(id, mowerUpdate)", throwing = "ex")
    public void afterThrowingMowerUpdate(JoinPoint jp, int id, MowerDTO mowerUpdate, ClientErrorException.NotFoundException ex) {
        log.info("afterThrowingMowerUpdate: {}, {} => {}", id, mowerUpdate, ex.toString());
    }

    @After("mowerUpdate(id, mowerUpdate)")
    public void afterMowerUpdate(JoinPoint jp, int id, MowerDTO mowerUpdate) {
        log.info("afterReturningMowerUpdate: {}, {}", id, mowerUpdate);
    }

    @Around("mowerUpdate(id, mowerUpdate)")
    public Object aroundMowerUpdate(ProceedingJoinPoint pjp, int id, MowerDTO mowerUpdate) throws Throwable {
        Object result = null;
        try {
            log.info("entering aroundMowerUpdate: {}, {}", id, mowerUpdate);
            result = pjp.proceed(pjp.getArgs());
            log.info("returning after successful aroundMowerUpdate: {}, {} => {}", id, mowerUpdate, result);
            return result;
        } catch (Throwable ex) {
            log.info("returning after aroundMowerUpdate excdeption: {}, {} => {}", id, mowerUpdate, ex.toString());
            result = ex;
            throw ex;
        } finally {
            log.info("returning after aroundMowerUpdate: {}, {} => {}",
                    id, mowerUpdate, (result==null ? null :result.toString()));
        }
    }
}
