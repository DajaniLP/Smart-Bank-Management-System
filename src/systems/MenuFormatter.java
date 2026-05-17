package systems;

public class MenuFormatter {
    private static final int WIDTH = 68;

    public static void printHeader(String title) {
        printDivider();
        int spaces = (WIDTH - title.length() - 2) / 2;
        String padding = " ".repeat(Math.max(0, spaces));
        System.out.println("||" + padding + title.toUpperCase() + padding + (title.length() % 2 != 0 ? " " : "") + "||");
        printDivider();
    }

    public static void printDivider() {
        System.out.println("====================================================================");
    }

    public static void printMenuOption(String num, String label) {
        int paddingSize = WIDTH - label.length() - num.length() - 9;
        String padding = " ".repeat(Math.max(0, paddingSize));
        System.out.println("||  [" + num + "] " + label + padding + "||");
    }

    public static void printStatRow(String label, String value) {
        int paddingSize = WIDTH - label.length() - value.length() - 7;
        String padding = " ".repeat(Math.max(0, paddingSize));
        System.out.println("|| " + label + ":" + padding + value + " ||");
    }

    public static void printProgressBar(String label, double used, double max) {
        double ratio = Math.min(1.0, Math.max(0.0, used / max));
        int barWidth = 20;
        int filled = (int) (ratio * barWidth);
        
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barWidth; i++) {
            if (i < filled) bar.append("#");
            else bar.append("-");
        }
        bar.append("]");
        
        String pctStr = String.format(" %.1f%%", ratio * 100);
        String rightSide = bar.toString() + pctStr;
        
        int paddingSize = WIDTH - label.length() - rightSide.length() - 7;
        String padding = " ".repeat(Math.max(0, paddingSize));
        System.out.println("|| " + label + ":" + padding + rightSide + " ||");
    }

    public static void printFooter() {
        printDivider();
        System.out.println();
    }
}