package com.LibraryManagementSystem.AiSystems.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedData<T>
{
    T UpdateData;
    Boolean UpdatedDataStatus;
}

