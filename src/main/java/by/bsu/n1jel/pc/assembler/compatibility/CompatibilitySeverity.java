package by.bsu.n1jel.pc.assembler.compatibility;

public enum CompatibilitySeverity {

    // Так собрать нельзя.

    ERROR,

    // В сборке не хватает обязательного компонента.
    INCOMPLETE,

    // Собрать можно, но есть нюансы

    WARNING
}
