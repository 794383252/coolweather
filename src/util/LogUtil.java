package util;

import android.util.Log;

public class LogUtil {
	private int grade = 0;
	private int verbose = 1;
	private int debug = 2;
	private int info = 3;
	private int warn = 4;
	private int error = 5;

	public void v(String message) {
		if (verbose > grade) {
			Log.i("ln", message);
		}
	}

	public void d(String message) {
		if (debug > grade) {
			Log.i("ln", message);
		}
	}

	public void i(String message) {
		if (info > grade) {
			Log.i("ln", message);
		}
	}

	public void w(String message) {
		if (warn > grade) {
			Log.i("ln", message);
		}
	}

	public void e(String message) {
		if (error > grade) {
			Log.i("ln", message);
		}
	}

}
