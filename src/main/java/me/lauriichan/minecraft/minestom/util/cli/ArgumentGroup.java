package me.lauriichan.minecraft.minestom.util.cli;

import java.util.Objects;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.logger.util.StringUtil;
import me.lauriichan.minecraft.minestom.util.cli.ArgumentReader.Token;

public final class ArgumentGroup {

    public static ArgumentGroup newRoot(String name, String description) {
        return new ArgumentGroup(name, description);
    }

    public static ArgumentGroup newRoot(String name, String[] description) {
        return new ArgumentGroup(name, String.join("\n", description));
    }

    private final ArgumentGroup parent;

    private final String name;
    private final String description;

    private final ObjectArrayList<ArgumentGroup> groups;
    private final ObjectArrayList<IArgument<?>> arguments;

    private ArgumentGroup(String name, String description) {
        this.parent = null;
        this.name = name;
        this.description = Objects.requireNonNull(description);
        this.groups = new ObjectArrayList<>();
        this.arguments = null;
    }

    private ArgumentGroup(ArgumentGroup parent, String name, String description) {
        this.parent = parent;
        this.name = name;
        this.description = Objects.requireNonNull(description);
        this.groups = null;
        this.arguments = new ObjectArrayList<>();
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public ArgumentGroup newGroup(String name, String[] description) {
        return newGroup(name, String.join("\n", description));
    }

    public ArgumentGroup newGroup(String name, String description) {
        if (groups == null) {
            return parent.newGroup(name, description);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid argument name: " + name);
        }
        if (groups.stream().anyMatch(grp -> grp.name.equals(name))) {
            throw new IllegalArgumentException("Duplicated group name: " + name);
        }
        ArgumentGroup group = new ArgumentGroup(this, name, description);
        groups.add(group);
        return group;
    }

    public <V extends IArgument<?>> V argument(V argument) {
        if (arguments == null) {
            throw new IllegalStateException("Can only be done on child group");
        }
        Objects.requireNonNull(argument);
        if (isArgumentNameTaken(argument.name())) {
            throw new IllegalArgumentException("Duplicated argument name: " + argument.name());
        }
        arguments.add(argument);
        return argument;
    }

    public IArgument<?> get(String name) {
        if (parent != null) {
            return parent.get(name);
        }
        return groups.stream().flatMap(grp -> grp.arguments.stream()).filter(arg -> arg.name().equals(name)).findFirst().orElse(null);
    }

    public void readCommandLine(String[] args) {
        if (parent != null) {
            parent.readCommandLine(args);
            return;
        }
        ArgumentReader reader = new ArgumentReader(args);
        Token token;
        String name = null;
        while ((token = reader.token()) != Token.END) {
            if (token == Token.NAME || token == Token.NAME_SHORT) {
                if (name != null) {
                    setValue(name, "");
                }
                name = reader.value();
                continue;
            }
            setValue(name, reader.value());
            name = null;
        }
        if (name != null) {
            setValue(name, "");
        }
    }

    public void printHelp() {
        if (parent != null) {
            parent.printHelp();
            return;
        }
        print("--=| {0}", name);
        if (!description.isBlank()) {
            print();
            print(description);
        }
        for (int j = 0; j < groups.size(); j++) {
            ArgumentGroup group = groups.get(j);
            print();
            print();
            print("# {0}\n", group.name());
            if (!group.description().isBlank()) {
                print(group.description());
                print();
            }
            for (int i = 0; i < group.arguments.size(); i++) {
                IArgument<?> arg = group.arguments.get(i);
                if (arg.valueName() == null) {
                    print("--{0}", arg.name());
                } else {
                    print("--{0}=[{1}]", arg.name(), arg.valueName());
                }
                if (arg.defaultValue() != null) {
                    print("  Default value: '{0}'", arg.defaultValue());
                }
                printPrefix("  ", arg.description());
                if (i + 1 != group.arguments.size()) {
                    print();
                }
            }
        }
    }
    
    private void print() {
        System.out.println();
    }
    
    private void print(String text, Object... placeholder) {
        System.out.println(StringUtil.format(text, placeholder));
    }
    
    private void print(String text) {
        System.out.println(text);
    }
    
    private void printPrefix(String prefix, String text) {
        if (text.contains("\n")) {
            for (String line : text.split("\n")) {
                System.out.print(prefix);
                System.out.println(line);
            }
            return;
        }
        System.out.print(prefix);
        System.out.println(text);
    }

    private void setValue(String name, String value) {
        IArgument<?> arg = get(name);
        if (arg == null) {
            return;
        }
        while (!(arg instanceof Argument<?> setableArg)) {
            arg = ((IDelegateArgument<?, ?>) arg).delegate();
        }
        setableArg.setValue(value);
    }

    private boolean isArgumentNameTaken(String name) {
        if (parent != null) {
            return parent.isArgumentNameTaken(name);
        }
        for (ArgumentGroup group : groups) {
            if (group.arguments.stream().anyMatch(arg -> arg.name().equals(name))) {
                return true;
            }
        }
        return false;
    }

}
