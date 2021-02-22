package com.github.signer4j.gui.utils;

import java.awt.Image;
import java.io.InputStream;

import javax.swing.ImageIcon;

public interface IPicture {

  InputStream asStream();

  Image asImage();

  ImageIcon asIcon();

}