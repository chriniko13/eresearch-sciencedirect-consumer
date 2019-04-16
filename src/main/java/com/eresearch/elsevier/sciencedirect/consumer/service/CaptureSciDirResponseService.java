package com.eresearch.elsevier.sciencedirect.consumer.service;

import lombok.extern.log4j.Log4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Log4j

@Component
@Aspect
public class CaptureSciDirResponseService {

    @Value("${capture.scidir-response}")
    private boolean captureSciDir;

    @Value("${capture-service.path-to-store-files}")
    private String pathToStoreFiles;

    @Pointcut("execution(String com.eresearch.elsevier.sciencedirect.consumer.connector.communicator.BasicCommunicator.communicateWithElsevier(java.net.URI))")
    private void interceptSciDirCommunication() {
    }

    @Around("interceptSciDirCommunication()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {

        Object result = pjp.proceed();

        if (captureSciDir) {
            //Example: https://api.elsevier.com/content/search/sciencedirect?apikey=f560b7d8fb2ee94533209bc0fdf5087f&query=aus(Grammati%20Pantziou)&view=complete
            URI uri = (URI) pjp.getArgs()[0];

            String queryParamsConcatenated = uri.toString().split("\\?")[1];

            String[] queryParams = queryParamsConcatenated.split("&");

            String candidateForFilename = UUID.randomUUID().toString();

            for (String queryParam : queryParams) {
                if (queryParam.contains("query")) {
                    candidateForFilename = queryParam.split("=")[1];
                }
            }

            //System.out.println("    >>>" + uri);
            log("getSciDir__" + candidateForFilename, (String) result, "json");
        }

        return result;
    }


    private void log(String filename, String contents, String fileType) {
        try {
            Path path = Paths.get(pathToStoreFiles, filename + "." + fileType);

            Files.deleteIfExists(path);
            Files.createFile(path);

            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
                bufferedWriter.write(contents);
                bufferedWriter.newLine();
            }
        } catch (Exception error) {
            log.error("error occurred: " + error.getMessage(), error);
        }
    }

}
