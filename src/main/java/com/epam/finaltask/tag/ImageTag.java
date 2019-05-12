package com.epam.finaltask.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class ImageTag extends TagSupport {

    private static final Integer DEFAULT_WIDTH = 128;
    private static final Integer DEFAULT_HEIGHT = 128;

    private boolean test;
    private String alt;
    private Integer width;
    private Integer height;
    private String src;
    private String defaultSrc;
    private String imgClass;

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
