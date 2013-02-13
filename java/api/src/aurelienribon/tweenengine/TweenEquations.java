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
 * Collection of built-in easing equations
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface TweenEquations {
	public static final Linear easeNone = Linear.INOUT;
	public static final Quad easeInQuad = Quad.IN;
	public static final Quad easeOutQuad = Quad.OUT;
	public static final Quad easeInOutQuad = Quad.INOUT;
	public static final Cubic easeInCubic = Cubic.IN;
	public static final Cubic easeOutCubic = Cubic.OUT;
	public static final Cubic easeInOutCubic = Cubic.INOUT;
	public static final Quart easeInQuart = Quart.IN;
	public static final Quart easeOutQuart = Quart.OUT;
	public static final Quart easeInOutQuart = Quart.INOUT;
	public static final Quint easeInQuint = Quint.IN;
	public static final Quint easeOutQuint = Quint.OUT;
	public static final Quint easeInOutQuint = Quint.INOUT;
	public static final Circ easeInCirc = Circ.IN;
	public static final Circ easeOutCirc = Circ.OUT;
	public static final Circ easeInOutCirc = Circ.INOUT;
	public static final Sine easeInSine = Sine.IN;
	public static final Sine easeOutSine = Sine.OUT;
	public static final Sine easeInOutSine = Sine.INOUT;
	public static final Expo easeInExpo = Expo.IN;
	public static final Expo easeOutExpo = Expo.OUT;
	public static final Expo easeInOutExpo = Expo.INOUT;
	public static final Back easeInBack = Back.IN;
	public static final Back easeOutBack = Back.OUT;
	public static final Back easeInOutBack = Back.INOUT;
	public static final Bounce easeInBounce = Bounce.IN;
	public static final Bounce easeOutBounce = Bounce.OUT;
	public static final Bounce easeInOutBounce = Bounce.INOUT;
	public static final Elastic easeInElastic = Elastic.IN;
	public static final Elastic easeOutElastic = Elastic.OUT;
	public static final Elastic easeInOutElastic = Elastic.INOUT;
}
