import java.awt.*;


//THIS IS A SAMPLE CUTSCENE CLASS FOR DIALOGUE THAT I BASICALLY COPY AND PASTED FROM THE TAKEOFF (AND USED SOME COPILOT HELP BC I COULDNT UNDERSTAND SOME OF THE
//JAVA2D SHIZZ) AND ONLINE JAVA2D DOCS FOR SOME TEXT THINGS

public class DialogCutscene extends Cutscene {
    private String[] dialogues;
    private int currentDialogIndex;
    private float textAlpha;
    private int frameCount;
    private boolean fadingIn, fadingOut;

    public DialogCutscene(GamePanel panel, TileMap tileMap, String[] dialogues) {
        super(panel, tileMap);
        this.dialogues = dialogues;
        reset();
    }

    @Override
    protected void reset() {
        currentDialogIndex = 0;
        textAlpha = 0f;
        frameCount = 0;
        fadingIn = true;
        fadingOut = false;
        isPlaying = false;
        isDone = false;
    }

    @Override
    public void update() {
        if (!isPlaying) return;
        frameCount++;

        if (fadingIn) {
            textAlpha += 0.05f;
            if (textAlpha >= 1f) {
                textAlpha = 1f;
                fadingIn = false;
                frameCount = 0;
            }
        } else if (fadingOut) {
            textAlpha -= 0.05f;
            if (textAlpha <= 0f) {
                textAlpha = 0f;
                fadingOut = false;
                isPlaying = false;
                isDone = true;
            }
        } else {
            if (frameCount > 100) {
                currentDialogIndex++;
                if (currentDialogIndex >= dialogues.length) {
                    fadingOut = true;
                } else {
                    fadingIn = true;
                    textAlpha = 0f;
                }
                frameCount = 0;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) { //dont ask me about this, talk to java and copilot bc I could not figure this out by myself
        if (!isPlaying) return;
        if (tileMap != null) tileMap.draw(g2);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f * textAlpha));
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(50, panel.getHeight() - 150, panel.getWidth() - 100, 100, 20, 20);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha));
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));

        if (currentDialogIndex < dialogues.length) {
            drawWrappedText(g2, dialogues[currentDialogIndex], 70, panel.getHeight() - 120, panel.getWidth() - 140);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = y;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            int lineWidth = fm.stringWidth(testLine);

            if (lineWidth > maxWidth && line.length() > 0) {
                g2.drawString(line.toString(), x, lineY);
                line = new StringBuilder(word);
                lineY += fm.getHeight();
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, lineY);
        }
    }
}
