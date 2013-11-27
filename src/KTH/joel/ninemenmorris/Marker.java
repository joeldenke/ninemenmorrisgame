package KTH.joel.ninemenmorris;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class Marker extends Thread
{

    private SurfaceHolder sh;
    private GameBoard ball;

    private Canvas canvas;

    private boolean run = false;

    public Marker(SurfaceHolder _holder, GameBoard _ball) {

        sh = _holder;
        ball = _ball;
    }

    public void setRunnable(boolean _run) {

        run = _run;
    }

    public void run() {

        while(run) {

            canvas = null;

            try {

                canvas = sh.lockCanvas(null);

                synchronized(sh) {

                    ball.onDraw(canvas);
                }

            } finally {

                if(canvas != null) {

                    sh.unlockCanvasAndPost(canvas);
                }

            }

        }
    }

    public Canvas getCanvas() {

        if(canvas != null) {

            return canvas;

        } else {

            return null;
        }
    }
}
