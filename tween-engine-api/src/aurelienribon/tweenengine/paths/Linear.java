package aurelienribon.tweenengine.paths;

import aurelienribon.tweenengine.TweenPath;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Linear implements TweenPath {
	@Override
	public float compute(float t, float[] points, int pointsCnt) {
		int pathId = (int) Math.floor((pointsCnt-1) * t);
		pathId = Math.max(pathId, 0);
		pathId = Math.min(pathId, pointsCnt-2);

		t = t * (pointsCnt-1) - pathId;

		return points[pathId] + t * (points[pathId+1] - points[pathId]);
	}
}
