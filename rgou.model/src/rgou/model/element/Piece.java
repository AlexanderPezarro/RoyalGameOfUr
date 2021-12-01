package rgou.model.element;

public class Piece {
    private boolean owner1;
    private boolean finished;

    public Piece(boolean owner1, boolean finished) {
        this.owner1 = owner1;
        this.finished = finished;
    }

    public boolean isOwner1() {
        return owner1;
    }

    public void setOwner1(boolean owner1) {
        this.owner1 = owner1;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
