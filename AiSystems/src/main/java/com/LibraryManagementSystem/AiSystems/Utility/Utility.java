package com.LibraryManagementSystem.AiSystems.Utility;

import org.springframework.stereotype.Service;

@Service
public class Utility
{
    public static String extractFileName(String Url)
    {
        return Url.substring(Url.lastIndexOf("/") + 1);
    }
}
