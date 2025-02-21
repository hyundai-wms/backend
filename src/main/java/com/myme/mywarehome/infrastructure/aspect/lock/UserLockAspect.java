package com.myme.mywarehome.infrastructure.aspect.lock;

import com.myme.mywarehome.domains.user.application.exception.LoginInProgressException;
import com.myme.mywarehome.domains.user.application.exception.UnexpectedLoginException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserLockAspect {
    private final Map<String, Lock> userLocks = new ConcurrentHashMap<>();

    @Around("@annotation(userLock) && args(id,..)")
    public Object lock(ProceedingJoinPoint joinPoint, UserLock userLock, String id) throws Throwable {
        Lock lock = userLocks.computeIfAbsent(id, k -> new ReentrantLock());

        try {
            if (!lock.tryLock(userLock.timeoutSeconds(), TimeUnit.SECONDS)) {
                throw new LoginInProgressException();
            }

            try {
                return joinPoint.proceed();
            } finally {
                lock.unlock();
                userLocks.remove(id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UnexpectedLoginException();
        }
    }
}
