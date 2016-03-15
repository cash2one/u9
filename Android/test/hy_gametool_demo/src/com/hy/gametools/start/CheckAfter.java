package com.hy.gametools.start;

public abstract interface CheckAfter<T>
{
    public abstract void afterFailed(String paramString,
            Exception paramException);

    public abstract void afterSuccess(T paramT);
}
