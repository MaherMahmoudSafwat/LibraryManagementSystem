package com.LibraryManagementSystem.AiSystems.Interfaces;

public interface Dtos<R,T>
{
    public R ToDtoResponse(T Dto);
    public T ToEntityRequest(R Dto);
}
