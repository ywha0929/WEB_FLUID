#VERTEX_CODE

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 textureCoordinate;
varying vec2 maskCoordinate;

void main() {
    gl_Position = aPosition;
    maskCoordinate = aTextureCoord.xy;
    textureCoordinate = aPosition.xy * 0.5 + 0.5;
}

#END_CODE

#FRAGMENT_CODE

precision highp float;

varying vec2 textureCoordinate;
varying vec2 maskCoordinate;

uniform sampler2D inputTexture;
uniform sampler2D blurTexture;
uniform sampler2D blurTexture2;
uniform sampler2D maskTexture;

uniform float nasolabialStrength;
uniform float eyeBagStrength;

void main()
{
    vec4 color = texture2D(inputTexture, textureCoordinate);
    vec4 maskColor = texture2D(maskTexture, maskCoordinate);

    if (maskColor.g > 0.01 && eyeBagStrength >= 0.01)
    {
        vec3 blurColor1 = texture2D(blurTexture, textureCoordinate).rgb;
        vec3 blurColor2 = texture2D(blurTexture2, textureCoordinate).rgb;

        vec3 diffColor = clamp((blurColor2 - blurColor1) * 1.3 + 0.03 * blurColor2, 0.0, 0.2);
        vec3 resultColor = mix(color.rgb, min(color.rgb + diffColor, 1.0), eyeBagStrength * maskColor.g);

        color = vec4(resultColor, color.a);
    }
    else if (maskColor.b > 0.01 && maskColor.r < 0.01 && nasolabialStrength >= 0.01)
    {
        vec3 blurColor1 = texture2D(blurTexture, textureCoordinate).rgb;
        vec3 blurColor2 = texture2D(blurTexture2, textureCoordinate).rgb;

        vec3 diffColor = clamp((blurColor2 - blurColor1) * 1.4 + 0.05 * blurColor2, 0.0, 0.3);
        vec3 resultColor = mix(color.rgb, min(color.rgb + diffColor, 1.0), nasolabialStrength * maskColor.b);

        color = vec4(resultColor, color.a);
    }
    gl_FragColor = color;
}

#END_CODE