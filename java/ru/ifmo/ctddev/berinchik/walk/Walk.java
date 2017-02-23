package ru.ifmo.ctddev.berinchik.walk;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by vadim on 12.02.17.
 */
public class Walk {


    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Invalid number of arguments");
            return;
        }

        try {
            final BufferedReader br = new BufferedReader(new FileReader(args[0]));
            try {
                final Writer writer = new FileWriter(args[1]);
                try {
                    while (br.ready()) {
                        final String name = br.readLine();
                        final Path path;
                        try {
                            path = Paths.get(name);
                        } catch(InvalidPathException e) {
                            System.err.println("Invalid path exception: " + name);
                            continue;
                        }

                        String hash;
                        try {
                            hash = String.format("%08x", FVNHash.calc(path.toFile()));
                        } catch (IOException e) {
                            hash = "00000000";
                        }

                        try {
                            writer.write(hash + " " + path + "\n");
                        } catch (IOException e) {
                            System.err.println("Error writing in output file" + path);
                        }

                    }
                } catch (IOException e) {
                    System.err.println("BufferedReader can't read input file exception");
                }

                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Output file closing exception");
                }
            } catch (IOException e) {
                System.err.println("Output file opening exception");
            }

            try {
                br.close();
            } catch (IOException e) {
                System.err.println("Input file closing exception");
            }
        } catch (IOException e) {
            System.err.println("Input file opening exception");
        }

    }
}
