package com.sp.dataimport.cli;

import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Cli.GroupBuilder;
import io.airlift.airline.Help;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
public class VanguardCLI {
	
	public static void main(String[] args) {
		VanguardCLI cli = new VanguardCLI();
		cli.build(args);
	}
	
	public void build(String[] arguments) {
				
		CliBuilder<Runnable> builder = Cli.<Runnable>builder("<import>")
                .withDescription("Import commandline interface.")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class);

        buildCommands(builder);
        Cli<Runnable> parser = builder.build();
        try {
            parser.parse(arguments).run();
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter errorWriter = new PrintWriter(writer);
            e.printStackTrace(errorWriter);
            System.err.println("Arguments: " + Arrays.toString(arguments) + "\nError: " + writer);
            System.exit(-1);
        }
	}
	
	private static void buildCommands(CliBuilder<Runnable> builder) {
		ServiceLoader<ICommandGroup> serviceLoader = ServiceLoader
				.load(ICommandGroup.class);
		StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(serviceLoader.iterator(),
						Spliterator.NONNULL), false).forEach(
				commandGroup -> {
					GroupBuilder groupBuilder = builder
							.withGroup(commandGroup.name())
							.withDescription(commandGroup.description())
							.withDefaultCommand(Help.class);
					groupBuilder.withCommands(commandGroup.commands());
				});
	}

}
