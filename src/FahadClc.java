import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
public class FahadClc extends JFrame implements ActionListener {
    // ── State ─────────────────────────────────────────────────────────────────
    private StringBuilder expression  = new StringBuilder();
    private double         memory      = 0;
    private double         ansValue    = 0;
    private boolean        shiftActive = false;
    private boolean        alphaActive = false;
    private boolean        isDegree    = true;   // true=DEG, false=RAD
    private boolean        resultShown = false;
    // ── UI components ─────────────────────────────────────────────────────────
    private JTextField mainDisplay;   // expression / result line
    private JTextField subDisplay;    // mode indicators (DEG, SHIFT…)
    private JLabel     shiftLabel;
    private JLabel     alphaLabel;
    private JLabel     mLabel;        // Memory indicator
    // ── Colours matching the real calculator ─────────────────────────────────
    private static final Color BG_BODY       = new Color(30,  30,  30);
    private static final Color BG_DISPLAY    = new Color(164, 185, 142);
    private static final Color BTN_WHITE     = new Color(240, 240, 235);
    private static final Color BTN_BLUE      = new Color(52,  101, 200);
    private static final Color BTN_ORANGE    = new Color(200, 100, 20);
    private static final Color BTN_DARK      = new Color(50,  50,  50);
    private static final Color TEXT_SHIFT    = new Color(255, 165,  30);   // orange
    private static final Color TEXT_ALPHA    = new Color(160, 200, 255);   // light blue
    private static final Color TEXT_NORMAL   = Color.WHITE;
    // ── Constructor ───────────────────────────────────────────────────────────
    public FahadClc() {
        super("Fahad clc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_BODY);
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 14, 10));
        root.add(buildTopHeader(), BorderLayout.NORTH);
        root.add(buildDisplayPanel(), BorderLayout.CENTER);
        JPanel btnArea = buildAllButtons();
        root.add(btnArea, BorderLayout.SOUTH);
        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    // ═════════════════════════════════════════════════════════════════════════
    //  UI builders
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildTopHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_BODY);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        JLabel logo = new JLabel("Fahad clc", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 13));
        logo.setForeground(new Color(160, 200, 255)); // Light blue text
        p.add(logo, BorderLayout.CENTER);

        // Solar panel mockup
        JPanel solar = new JPanel();
        solar.setBackground(new Color(40, 30, 20));
        solar.setPreferredSize(new Dimension(80, 25));
        solar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JPanel solarWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        solarWrapper.setBackground(BG_BODY);
        solarWrapper.add(solar);
        p.add(solarWrapper, BorderLayout.EAST);

        return p;
    }
    private JPanel buildDisplayPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 2));
        outer.setBackground(new Color(20, 20, 20));
        outer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(80, 80, 80), 2, true),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        // ── indicator bar ──
        JPanel indicBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        indicBar.setBackground(BG_DISPLAY);
        shiftLabel = makeIndicLabel("S", TEXT_SHIFT); // Simplified to S matching screen
        alphaLabel = makeIndicLabel("A", TEXT_ALPHA);
        mLabel     = makeIndicLabel("M",     Color.BLACK);
        JLabel degLabel = makeIndicLabel("D", Color.BLACK);
        indicBar.add(shiftLabel);
        indicBar.add(alphaLabel);
        indicBar.add(mLabel);
        indicBar.add(degLabel);
        shiftLabel.setVisible(false);
        alphaLabel.setVisible(false);
        mLabel.setVisible(false);
        // ── sub-display (formula line) ──
        subDisplay = new JTextField("");
        subDisplay.setFont(new Font("Monospaced", Font.PLAIN, 16));
        subDisplay.setEditable(false);
        subDisplay.setBackground(BG_DISPLAY);
        subDisplay.setForeground(Color.DARK_GRAY);
        subDisplay.setBorder(null);
        subDisplay.setHorizontalAlignment(JTextField.LEFT);
        // ── main display (result line) ──
        mainDisplay = new JTextField("0");
        mainDisplay.setFont(new Font("Monospaced", Font.BOLD, 32));
        mainDisplay.setEditable(false);
        mainDisplay.setBackground(BG_DISPLAY);
        mainDisplay.setForeground(new Color(10, 30, 10));
        mainDisplay.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 4));
        mainDisplay.setHorizontalAlignment(JTextField.RIGHT);
        JPanel dispContent = new JPanel(new BorderLayout(0, 0));
        dispContent.setBackground(BG_DISPLAY);
        dispContent.add(indicBar, BorderLayout.NORTH);
        dispContent.add(subDisplay, BorderLayout.CENTER);
        dispContent.add(mainDisplay, BorderLayout.SOUTH);
        outer.add(dispContent, BorderLayout.CENTER);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_BODY);
        wrapper.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
        wrapper.add(outer);
        return wrapper;
    }
    private JLabel makeIndicLabel(String text, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(fg);
        return l;
    }
    // ── All button rows ───────────────────────────────────────────────────────
    private JPanel buildAllButtons() {
        JPanel all = new JPanel();
        all.setLayout(new BoxLayout(all, BoxLayout.Y_AXIS));
        all.setBackground(BG_BODY);
        all.add(buildSystemRow());
        all.add(vgap(8)); // removed nav row, added slightly more spacing here
        all.add(buildRow1());
        all.add(vgap(3));
        all.add(buildRow2());
        all.add(vgap(3));
        all.add(buildRow3());
        all.add(vgap(3));
        all.add(buildRow4());
        all.add(vgap(3));
        all.add(buildNumpadRows());
        return all;
    }
    private Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }
    /** SHIFT  ALPHA  MENU/SETUP  ON */
    private JPanel buildSystemRow() {
        JPanel p = rowPanel(15);
        p.add(makeBtn("SHIFT",  BTN_DARK, Color.WHITE,  null, null, 10, true));
        p.add(makeBtn("ALPHA",  BTN_DARK, Color.WHITE,  null, null, 10, true));
        p.add(Box.createHorizontalGlue());
        p.add(makeBtn("MENU SETUP", BTN_DARK, Color.WHITE, null, null, 10, true));
        p.add(makeBtn("ON",     BTN_DARK, Color.WHITE,  null, null, 10, true));
        return p;
    }
    /** OPTN  CALC  ∫dx  x */
    private JPanel buildRow1() {
        JPanel p = rowPanel(10);
        p.add(makeBtn2("OPTN", "QR", "", "OPTN", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("CALC", "SOLVE", "=", "CALC", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("∫dx", "", "", "INTEGRAL", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("x", "", "", "VAR_X", BTN_DARK, Color.WHITE));
        return p;
    }
    /** a b/c  √  x²  xⁿ  log  ln */
    private JPanel buildRow2() {
        JPanel p = rowPanel(8);
        p.add(makeBtn2("■/□", "", "", "FRAC", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("√□", "³√", "", "SQRT", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("x²", "", "", "SQUARE", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("x■", "ˣ√", "", "POWER", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("log", "10■", "", "LOG", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("ln", "e■", "", "LN", BTN_DARK, Color.WHITE));
        return p;
    }
    /** (-)  °'"  x⁻¹  sin  cos  tan */
    private JPanel buildRow3() {
        JPanel p = rowPanel(8);
        p.add(makeBtn2("(-)", "", "A", "NEGATE", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("°'\"", "", "B", "DEGREE", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("x⁻¹", "", "C", "INVERSE", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("sin", "sin⁻¹", "D", "SIN", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("cos", "cos⁻¹", "E", "COS", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("tan", "tan⁻¹", "F", "TAN", BTN_DARK, Color.WHITE));
        return p;
    }
    /** STO  ENG  (  )  S⟺D  M+ */
    private JPanel buildRow4() {
        JPanel p = rowPanel(8);
        p.add(makeBtn2("STO", "RECALL", "", "STO", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("ENG", "", "i", "ENG", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("(", "", "x", "LPAREN", BTN_DARK, Color.WHITE));
        p.add(makeBtn2(")", "", "y", "RPAREN", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("S⇔D", "", "M", "STOD", BTN_DARK, Color.WHITE));
        p.add(makeBtn2("M+", "M-", "", "MPLUS", BTN_DARK, Color.WHITE));
        return p;
    }
    /** Number pad with F design using 8,5,2,9,6 */
    private JPanel buildNumpadRows() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 5, 8, 8));
        p.setBackground(BG_BODY);
        Color fBg = BTN_ORANGE;
        Color fFg = Color.WHITE;
        // Row 7 8 9 DEL AC
        p.add(makeNumBtn("7", "7"));
        p.add(makeColoredNumBtn("8", "8", fBg, fFg)); // F part
        p.add(makeColoredNumBtn("9", "9", fBg, fFg)); // F part
        p.add(makeSpecialBtn("DEL", "DEL", BTN_BLUE));
        p.add(makeSpecialBtn("AC",  "AC",  BTN_BLUE));
        // Row 4 5 6 × ÷
        p.add(makeNumBtn("4", "4"));
        p.add(makeColoredNumBtn("5", "5", fBg, fFg)); // F part
        p.add(makeColoredNumBtn("6", "6", fBg, fFg)); // F part
        p.add(makeOpBtn("×", "MUL"));
        p.add(makeOpBtn("÷", "DIV"));
        // Row 1 2 3 + -
        p.add(makeNumBtn("1", "1"));
        p.add(makeColoredNumBtn("2", "2", fBg, fFg)); // F part
        p.add(makeNumBtn("3", "3"));
        p.add(makeOpBtn("+", "ADD"));
        p.add(makeOpBtn("-", "SUB"));
        // Row 0 . ×10ˣ Ans =
        p.add(makeNumBtn("0", "0"));
        p.add(makeNumBtn(".", "."));
        p.add(makeOpBtn("×10ˣ", "E10"));
        p.add(makeSpecialBtn("Ans", "ANS", BTN_WHITE));
        p.add(makeSpecialBtn("=", "EQ", BTN_WHITE));
        return p;
    }
    // ── Button factories ──────────────────────────────────────────────────────
    private JPanel rowPanel(int gap) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, gap, 0));
        p.setBackground(BG_BODY);
        return p;
    }
    private JButton makeBtn(String primary, Color bg, Color fg,
                            String shift, String alpha, int fontSize, boolean small) {
        JButton b = roundBtn(primary, bg, fg, fontSize);
        b.setActionCommand(primary.toUpperCase());
        b.addActionListener(this);
        if (small) b.setPreferredSize(new Dimension(45, 25)); // smaller for top row
        return b;
    }
    private JButton makeBtn2(String primary, String shiftLabel, String alphaLabel, String cmd,
                             Color bg, Color fg) {
        JButton b = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // body
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 12, getWidth(), getHeight()-12, 6, 6);

                // Labels above button
                g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
                FontMetrics fmS = g2.getFontMetrics();

                if (shiftLabel != null && !shiftLabel.isEmpty()) {
                    g2.setColor(TEXT_SHIFT);
                    g2.drawString(shiftLabel, 2, 10);
                }

                if (alphaLabel != null && !alphaLabel.isEmpty()) {
                    g2.setColor(TEXT_ALPHA);
                    g2.drawString(alphaLabel, getWidth() - fmS.stringWidth(alphaLabel) - 2, 10);
                }

                // primary label
                g2.setColor(fg);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()-fm.stringWidth(primary))/2;
                int y = 12 + (getHeight()-12+fm.getAscent()-fm.getDescent())/2;
                g2.drawString(primary, x, y);
                g2.dispose();
            }
        };
        b.setBackground(bg);
        b.setActionCommand(cmd);
        b.addActionListener(this);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(42, 38));
        return b;
    }
    private JButton makeNumBtn(String label, String cmd) {
        return makeColoredNumBtn(label, cmd, BTN_WHITE, Color.BLACK);
    }
    private JButton makeColoredNumBtn(String label, String cmd, Color bg, Color fg) {
        JButton b = roundBtn(label, bg, fg, 18);
        b.setActionCommand(cmd);
        b.addActionListener(this);
        b.setPreferredSize(new Dimension(55, 35));
        return b;
    }
    private JButton makeOpBtn(String label, String cmd) {
        JButton b = roundBtn(label, BTN_WHITE, Color.BLACK, 16);
        b.setActionCommand(cmd);
        b.addActionListener(this);
        b.setPreferredSize(new Dimension(55, 35));
        return b;
    }
    private JButton makeSpecialBtn(String label, String cmd, Color bg) {
        Color fg = (bg == BTN_WHITE) ? Color.BLACK : Color.WHITE;
        JButton b = roundBtn(label, bg, fg, 16);
        b.setActionCommand(cmd);
        b.addActionListener(this);
        b.setPreferredSize(new Dimension(55, 35));
        return b;
    }
    private JButton roundBtn(String label, Color bg, Color fg, int fontSize) {
        JButton b = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()-fm.stringWidth(label))/2;
                int y = (getHeight()+fm.getAscent()-fm.getDescent())/2;
                g2.drawString(label, x, y);
                g2.dispose();
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        return b;
    }
    // ═════════════════════════════════════════════════════════════════════════
    //  Action handling
    // ═════════════════════════════════════════════════════════════════════════
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "ON":
            case "AC":
                expression.setLength(0);
                mainDisplay.setText("0");
                subDisplay.setText("");
                shiftActive = false;
                alphaActive = false;
                resultShown = false;
                updateIndicators();
                break;
            case "SHIFT":
                shiftActive = !shiftActive;
                if (shiftActive) alphaActive = false;
                updateIndicators();
                break;
            case "ALPHA":
                alphaActive = !alphaActive;
                if (alphaActive) shiftActive = false;
                updateIndicators();
                break;
            case "MENU SETUP":
                showInfo("Settings Menu: (1) COMP (2) CMPLX (3) BASE-N");
                break;
            case "DEL":
                if (resultShown) {
                    expression.setLength(0);
                    mainDisplay.setText("0");
                    subDisplay.setText("");
                    resultShown = false;
                } else if (expression.length() > 0) {
                    expression.deleteCharAt(expression.length() - 1);
                    subDisplay.setText(expression.toString());
                }
                break;
            // Digits
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                if (resultShown) { expression.setLength(0); resultShown = false; mainDisplay.setText("0"); }
                appendExpr(cmd);
                break;
            case ".":
                if (resultShown) { expression.setLength(0); resultShown = false; mainDisplay.setText("0"); }
                appendExpr(".");
                break;
            // Operators
            case "ADD": appendExpr("+"); break;
            case "SUB": appendExpr("-"); break;
            case "MUL": appendExpr("×"); break;
            case "DIV": appendExpr("÷"); break;
            case "LPAREN": appendExpr("("); break;
            case "RPAREN": appendExpr(")"); break;
            case "E10":
                if (resultShown) { expression.setLength(0); resultShown = false; }
                appendExpr("×10^(");
                break;
            case "ANS":
                if (resultShown) { expression.setLength(0); resultShown = false; }
                appendExpr("Ans");
                break;
            case "EQ":
                calculate();
                break;
            // Scientific functions
            case "SIN":
                if (shiftActive) appendExpr("asin(");
                else appendExpr("sin(");
                break;
            case "COS":
                if (shiftActive) appendExpr("acos(");
                else appendExpr("cos(");
                break;
            case "TAN":
                if (shiftActive) appendExpr("atan(");
                else appendExpr("tan(");
                break;
            case "LOG":
                if (shiftActive) appendExpr("10^(");
                else appendExpr("log(");
                break;
            case "LN":
                if (shiftActive) appendExpr("e^(");
                else appendExpr("ln(");
                break;
            case "SQRT":
                if (shiftActive) appendExpr("cbrt(");
                else appendExpr("sqrt(");
                break;
            case "SQUARE":
                appendExpr("^2");
                break;
            case "POWER":
                appendExpr("^(");
                break;
            case "INVERSE":
                appendExpr("^(-1)");
                break;
            case "NEGATE":
                appendExpr("(-");
                break;
            case "FRAC":
                appendExpr("/");
                break;
            case "VAR_X":
                appendExpr("x");
                break;
            case "DEGREE":
                isDegree = !isDegree;
                showInfo("Mode changed to " + (isDegree ? "DEGREE" : "RADIAN"));
                break;
            default:
                break;
        }
        // Reset SHIFT/ALPHA after a function key is used
        boolean funcKey = !cmd.matches("[0-9]|\\.") &&
                !cmd.equals("SHIFT") && !cmd.equals("ALPHA") &&
                !cmd.equals("AC")    && !cmd.equals("ON")    &&
                !cmd.equals("DEL");
        if (funcKey && (shiftActive || alphaActive)) {
            shiftActive = false;
            alphaActive = false;
            updateIndicators();
        }
    }
    private void appendExpr(String s) {
        expression.append(s);
        subDisplay.setText(expression.toString());
    }
    private void calculate() {
        if (expression.length() == 0) return;
        try {
            String expr = expression.toString();
            // Pre-process expression for evaluation
            String procExpr = preprocess(expr);
            double result = evalExpr(procExpr);
            ansValue = result;
            mainDisplay.setText(formatDouble(result));
            resultShown = true;
        } catch (Exception ex) {
            mainDisplay.setText("Math ERROR");
            resultShown = true;
        }
    }
    private String preprocess(String s) {
        s = s.replace("×", "*").replace("÷", "/")
                .replace("Ans", String.valueOf(ansValue));
        return s;
    }
    private void updateIndicators() {
        shiftLabel.setVisible(shiftActive);
        alphaLabel.setVisible(alphaActive);
    }
    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    // Math Engine
    private double evalExpr(String expr) throws Exception {
        return parseExpr(expr);
    }

    private int pos;
    private String src;
    private double parseExpr(String expr) throws Exception {
        src = expr.trim();
        pos = 0;
        double val = parseAddSub();
        if (pos != src.length()) throw new Exception("Unexpected char at " + pos);
        return val;
    }
    private double parseAddSub() throws Exception {
        double left = parseMulDiv();
        while (pos < src.length()) {
            char op = src.charAt(pos);
            if (op == '+') { pos++; left += parseMulDiv(); }
            else if (op == '-') { pos++; left -= parseMulDiv(); }
            else break;
        }
        return left;
    }
    private double parseMulDiv() throws Exception {
        double left = parsePower();
        while (pos < src.length()) {
            char op = src.charAt(pos);
            if (op == '*') { pos++; left *= parsePower(); }
            else if (op == '/') {
                pos++;
                double r = parsePower();
                if (r == 0) throw new ArithmeticException("Division by zero");
                left /= r;
            } else break;
        }
        return left;
    }
    private double parsePower() throws Exception {
        double base = parseUnary();
        if (pos < src.length() && src.charAt(pos) == '^') {
            pos++;
            double exp = parseUnary();
            return Math.pow(base, exp);
        }
        return base;
    }
    private double parseUnary() throws Exception {
        if (pos < src.length() && src.charAt(pos) == '-') {
            pos++;
            return -parseUnary();
        }
        if (pos < src.length() && src.charAt(pos) == '+') {
            pos++;
            return parseUnary();
        }
        // Handle (-) negate button case represented as (-
        if (pos < src.length() && src.startsWith("(-", pos)) {
            pos += 2;
            return -parseAtom();
        }
        return parseAtom();
    }
    private double parseAtom() throws Exception {
        if (pos >= src.length()) throw new Exception("Unexpected end of expression");
        if (src.charAt(pos) == '(') {
            pos++;
            double val = parseAddSub();
            if (pos < src.length() && src.charAt(pos) == ')') pos++;
            return val;
        }
        if (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.') {
            return parseNumber();
        }
        return parseFunction();
    }
    private double parseNumber() throws Exception {
        int start = pos;
        while (pos < src.length() &&
                (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.')) {
            pos++;
        }
        return Double.parseDouble(src.substring(start, pos));
    }
    private double parseFunction() throws Exception {
        int start = pos;
        while (pos < src.length() &&
                (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '_')) {
            pos++;
        }
        String name = src.substring(start, pos);
        while (pos < src.length() && src.charAt(pos) == ' ') pos++;
        switch (name) {
            case "pi": return Math.PI;
            case "e": return Math.E;
            case "sin": return Math.sin(toRad(parseParen()));
            case "cos": return Math.cos(toRad(parseParen()));
            case "tan": return Math.tan(toRad(parseParen()));
            case "asin": return toDeg(Math.asin(parseParen()));
            case "acos": return toDeg(Math.acos(parseParen()));
            case "atan": return toDeg(Math.atan(parseParen()));
            case "log": return Math.log10(parseParen());
            case "ln": return Math.log(parseParen());
            case "sqrt": return Math.sqrt(parseParen());
            case "cbrt": return Math.cbrt(parseParen());
            default:
                throw new Exception("Unknown function: " + name);
        }
    }
    private double parseParen() throws Exception {
        if (pos < src.length() && src.charAt(pos) == '(') {
            pos++;
            double v = parseAddSub();
            if (pos < src.length() && src.charAt(pos) == ')') pos++;
            return v;
        }
        return parseAtom();
    }

    private double toRad(double x)  { return isDegree ? Math.toRadians(x) : x; }
    private double toDeg(double x)  { return isDegree ? Math.toDegrees(x) : x; }
    private String formatDouble(double v) {
        if (Double.isNaN(v)) return "Math ERROR";
        if (Double.isInfinite(v)) return v > 0 ? "∞" : "-∞";
        if (v == Math.floor(v) && Math.abs(v) < 1e15)
            return String.valueOf((long) v);
        return String.format("%.10g", v).replaceAll("0+$","").replaceAll("\\.$","");
    }
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(FahadClc::new);
    }
}