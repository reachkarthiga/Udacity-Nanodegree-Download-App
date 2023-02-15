package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {

    private var widthSize = 0
    private var heightSize = 0
    private var animatedBoxSize = 0
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
    }

    private var cornerRadius = 0f
    private var background = resources.getColor(R.color.colorPrimary)
    private var textColor = Color.WHITE
    private var spinnerColor = Color.WHITE
    private var loaderColor =  resources.getColor(R.color.colorPrimaryDark)

    private val valueAnimator_circle = ValueAnimator.ofFloat(0f,360f ).apply {
        duration= 2000
        interpolator = DecelerateInterpolator()
    }

    private val valueAnimator_box: ValueAnimator = ValueAnimator.ofFloat(0f,970f ).apply {
        duration= 2000
        interpolator = AccelerateInterpolator()
    }

    private var sweepAngle :Float = 0f

    private var paint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            17F, resources.displayMetrics)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        buttonState = ButtonState.Completed
        setUpAttributes(attrs)
    }

    private fun setUpAttributes(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0 , 0)

        spinnerColor = typedArray.getColor(R.styleable.LoadingButton_spinnerColor, Color.WHITE)
        textColor = typedArray.getColor(R.styleable.LoadingButton_colorText, Color.WHITE)
       background = typedArray.getColor(R.styleable.LoadingButton_buttonColor, resources.getColor(R.color.colorPrimary))
        cornerRadius = typedArray.getDimension(R.styleable.LoadingButton_cornerRadius, 0f)
        loaderColor = typedArray.getColor(R.styleable.LoadingButton_loaderColor, resources.getColor(R.color.colorPrimaryDark))

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = background
        canvas?.drawRoundRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), cornerRadius, cornerRadius, paint)


        when (buttonState) {
            ButtonState.Completed -> {
                paint.color = textColor
                canvas?.drawText("Download", widthSize.div(2).toFloat(), heightSize.div(1.75).toFloat() , paint)
            }

            ButtonState.Clicked -> {
                Log.i("button_state", "Clicked called")
            }

            ButtonState.Loading -> {
                paint.color = loaderColor
                canvas?.drawRect(0f, 0f, animatedBoxSize.toFloat(), heightSize.toFloat(), paint)
                paint.color = textColor
                canvas?.drawText("We are Loading", widthSize.div(2.5).toFloat(), heightSize.div(1.75).toFloat() , paint)
                paint.color = spinnerColor
                canvas?.drawArc(RectF(widthSize/1.6f, heightSize/4.5f, widthSize/1.4f, heightSize/1.3f), 0f, sweepAngle, true, paint)

            }

        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )

        widthSize = w
        heightSize = h

        setMeasuredDimension(w, h)

    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Loading

        valueAnimator_circle.addUpdateListener{
            sweepAngle = it.animatedValue.toString().toFloat()
        }

        valueAnimator_circle.start()

        valueAnimator_box.addUpdateListener(this)
        valueAnimator_box.start()

        invalidate()

        return true
    }

    override fun onAnimationUpdate(p0: ValueAnimator?) {

        animatedBoxSize = p0?.animatedValue.toString().toFloat().toInt()

        if (animatedBoxSize == widthSize) {
            buttonState = ButtonState.Completed
        }

        invalidate()

    }



}