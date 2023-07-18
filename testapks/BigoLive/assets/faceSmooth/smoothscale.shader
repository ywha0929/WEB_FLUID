#VERTEX_CODE

attribute vec4 aPosition;
attribute vec4 aTextureCoord;
varying vec2 textureCoordinate;

void main() {
    gl_Position = aPosition;
    textureCoordinate = aTextureCoord.xy;
}

#END_CODE

#FRAGMENT_CODE

precision highp float;

varying vec2 textureCoordinate;
uniform sampler2D inputTexture;

void main()
{
    gl_FragColor = texture2D(inputTexture, textureCoordinate);
}

#END_CODE