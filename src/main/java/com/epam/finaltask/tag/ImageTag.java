package com.epam.finaltask.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * Jsp tag class, Can be used to create tag which shows chosen image if condition is met, and default image if it isn't.
 */
public class ImageTag extends TagSupport {

    private static final Integer DEFAULT_WIDTH = 128;
    private static final Integer DEFAULT_HEIGHT = 128;

    /**
     * Condition to be tested.
     */
    private boolean test;

    /**
     * Text which will show up if image won't load. Also used by screen readers.
     */
    private String alt;

    /**
     * Image width.
     */
    private Integer width;

    /**
     * Image height.
     */
    private Integer height;

    /**
     * Image source. Will be used if tested condition is {@code true}.
     */
    private String src;

    /**
     * Default image source that will be used if tested condition is {@code false}.
     */
    private String defaultSrc;
    /**
     * img tag style class.
     */
    private String imgClass;

    /**
     *  Generates html to display image. If {@link #test} is {@code true}, then image specified in {@link #src} will be
     *  displayed. Otherwise {@link #defaultSrc} image will be used. Width and height can be specified using
     *  {@link #width} and {@link #height}. Style if the img tag can be specified using {@link #imgClass}. {@link #alt}
     *  content is passed to {@code alt} attribute of the img html tag.
     * @return SKIP_BODY constant.
     * @throws JspException if IOException will be thrown by JspWriter
     */
    @Override
    public int doStartTag() throws JspException {
        if (width == null) {
            width = DEFAULT_WIDTH;
        }
        if (height == null) {
            height = DEFAULT_HEIGHT;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<img ");
        if (imgClass != null) {
            stringBuilder.append("class=\"");
            stringBuilder.append(imgClass);
            stringBuilder.append("\" ");
        }
        stringBuilder.append("width=\"");
        stringBuilder.append(width);
        stringBuilder.append("\" height=\"");
        stringBuilder.append(height);
        if (alt != null) {
            stringBuilder.append("\" alt=\"");
            stringBuilder.append(alt);
        }
        stringBuilder.append("\" src=\"");
        if (test) {
            stringBuilder.append(src);
        } else {
            stringBuilder.append(defaultSrc);
        }
        stringBuilder.append("\"/>");
        try {
            JspWriter writer = pageContext.getOut();
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDefaultSrc() {
        return defaultSrc;
    }

    public void setDefaultSrc(String defaultSrc) {
        this.defaultSrc = defaultSrc;
    }

    public String getImgClass() {
        return imgClass;
    }

    public void setImgClass(String imgClass) {
        this.imgClass = imgClass;
    }
}
