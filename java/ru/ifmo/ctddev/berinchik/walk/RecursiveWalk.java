package ru.ifmo.ctddev.berinchik.walk;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveWalk {

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
                        final String fileName = br.readLine();

                        final Path start;

                        try {
                            start = Paths.get(fileName);
                        } catch (InvalidPathException e) {
                            try {
                                writer.write("00000000" + " " + fileName + "\n");
                            } catch (IOException ioe) {
                                System.err.println("Couldn'r write in output file");
                            }
                            System.err.println("Invalid path");
                            continue;
                        }

                        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                                String hash;
                                try {
                                    hash = String.format("%08x", FVNHash.calc(file.toFile()));
                                } catch (IOException e) {
                                    hash = "00000000";
                                }
                                try {
                                    writer.write(hash + " " + file + "\n");
                                } catch (IOException e) {
                                    System.err.println("Couldn't write in output file");
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                                String hash = "00000000";
                                try {
                                    writer.write(hash + " " + file + "\n");
                                } catch (IOException e) {
                                    System.err.println("Couldn't write in output file");
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });

                    }
                } catch (IOException e) {
                    System.err.println("Error with bufferedreader");
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