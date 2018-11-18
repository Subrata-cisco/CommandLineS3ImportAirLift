package com.sp.dataimport.cli;

import java.util.Collection;
public interface ICommandGroup {
    String name();

    String description();

    Collection<Class<? extends AbstractCommand>> commands();
}
