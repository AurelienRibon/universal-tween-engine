package aurelienribon.tweenengine;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;

/**
 * Base class for every easing equation. You can create your own equations
 * and directly use them in the Tween engine by inheriting from this class.
 *
 * @see Tween
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class TweenEquation {

	// -------------------------------------------------------------------------
	// Static stuff
	// -------------------------------------------------------------------------

	private static TweenEquation[] equations;

	/**
	 * Takes an equation name and gives you the corresponding TweenEquation.
	 * You probably won't need this, but tools will love that.
	 * @param equationName The name of an equation, like "Quad.INOUT".
	 * @return The parsed equation, or null if there is no match.
	 */
	public static TweenEquation parse(String equationName) {
		if (equations == null) {
			equations = new TweenEquation[] {Linear.INOUT,
				Quad.IN, Quad.OUT, Quad.INOUT,
				Cubic.IN, Cubic.OUT, Cubic.INOUT,
				Quart.IN, Quart.OUT, Quart.INOUT,
				Quint.IN, Quint.OUT, Quint.INOUT,
				Circ.IN, Circ.OUT, Circ.INOUT,
				Sine.IN, Sine.OUT, Sine.INOUT,
				Expo.IN, Expo.OUT, Expo.INOUT,
				Back.IN, Back.OUT, Back.INOUT,
				Bounce.IN, Bounce.OUT, Bounce.INOUT,
				Elastic.IN, Elastic.OUT, Elastic.INOUT
			};
		}

		for (int i=0; i<equations.length; i++) {
			if (equationName.equals(equations[i].toString()))
				return equations[i];
		}

		return null;
	}

	// -------------------------------------------------------------------------
	// Core
	// -------------------------------------------------------------------------

	/**
	 * Computes the next value of the interpolation.
	 * @param t Current time, in seconds.
	 * @param b Initial value.
	 * @param c Offset between target and initial value.
	 * @param d Total duration, in seconds.
	 * @return The current value.
	 */
    public abstract float compute(float t, float b, float c, float d);

	/**
	 * Returns true if the given string is the name of this equation (the name
	 * is returned in the toString() method, don't forget to override it).
	 * This method is usually used to save/load a tween to/from a text file.
	 */
	public boolean isValueOf(String str) {
		return str.equals(toString());
	}
}
