package com.googlehashcode.qualification2015;

import com.googlehashcode.qualification2015.algorithm.OptimizeDataCenter;
import com.googlehashcode.qualification2015.model.DataCenter;
import com.googlehashcode.qualification2015.model.Server;
import com.googlehashcode.qualification2015.utils.Utils;

import java.net.URL;

public class App {

    public static void main(String[] args) throws Exception {

        URL resource = App.class.getClassLoader().getResource("com/googlehashcode/qualification2015/dc.in");
        if (resource == null) throw new IllegalArgumentException("File not found!");

        // Read data file
        DataCenter dataCenter = Utils.readData(resource.getFile());

        // Launch algorithm on DataCenter
        int gc = OptimizeDataCenter.solve(dataCenter);
        System.out.println("Score = " + gc);

        // Save result in a file
        System.out.println(resource.getFile() + ".res");
        Utils.saveResult(dataCenter, resource.getFile() + ".res");
    }
}
