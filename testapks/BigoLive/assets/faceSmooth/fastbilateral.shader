#VERTEX_CODE
attribute vec4 aPosition;
attribute vec4 text_coord;
uniform float pixel_width_offset;
uniform float pixel_height_offset;
varying vec2 textureCoordinate;

varying vec4 pixel_shift_1;
varying vec4 pixel_shift_2;
varying vec4 pixel_shift_3;
varying vec4 pixel_shift_4;

void main()
{
    gl_Position = aPosition;
    textureCoordinate = text_coord.xy;

    vec2 pixel_step_offset = vec2(pixel_width_offset, pixel_height_offset);

    pixel_shift_1 = vec4(text_coord.xy - 1.0 * pixel_step_offset, text_coord.xy + 1.0 * pixel_step_offset);
    pixel_shift_2 = vec4(text_coord.xy - 2.0 * pixel_step_offset, text_coord.xy + 2.0 * pixel_step_offset);
    pixel_shift_3 = vec4(text_coord.xy - 3.0 * pixel_step_offset, text_coord.xy + 3.0 * pixel_step_offset);
    pixel_shift_4 = vec4(text_coord.xy - 4.0 * pixel_step_offset, text_coord.xy + 4.0 * pixel_step_offset);
}
#END_CODE

#FRAGMENT_CODE
precision mediump float;

uniform sampler2D input_image;

uniform float kernels[9];
varying mediump vec2 textureCoordinate;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;
varying mediump vec4 pixel_shift_3;
varying mediump vec4 pixel_shift_4;

#define MSIZE 9

float weight(vec3 v)
{
	float x = (abs(v.r) + abs(v.g) + abs(v.b)) / 3.0;
	return 0.1 / (x + 0.05);
}

void main()
{
    vec4 texture = texture2D(input_image, textureCoordinate);
    float a = texture.a;
    vec3 c = texture.rgb;

    const int kSize = (MSIZE-1)/2;

    vec3 final_colour = vec3(0.0, 0.0, 0.0);
    float Z = 0.0;

    vec3 cc;
    vec2 tt;
	float factor;

    cc = texture2D(input_image, pixel_shift_4.xy).rgb;
    factor = weight(cc-c) * kernels[0];
    Z += factor;
    final_colour += factor*cc;

    cc = texture2D(input_image, pixel_shift_3.xy).rgb;
    factor = weight(cc-c) * kernels[1];
    Z += factor;
    final_colour += factor*cc;

    cc = texture2D(input_image, pixel_shift_2.xy).rgb;
    factor = weight(cc-c) * kernels[2];
    Z += factor;
    final_colour += factor*cc;

    cc = texture2D(input_image, pixel_shift_1.xy).rgb;
    factor = weight(cc-c) * kernels[3];
    Z += factor;
    final_colour += factor*cc;

    factor = weight(vec3(0.0, 0.0, 0.0)) * kernels[4];
    Z += factor;
    final_colour += factor*c;

    cc = texture2D(input_image, pixel_shift_1.zw).rgb;
    factor = weight(cc-c) * kernels[5];
    Z += factor;
    final_colour += factor*cc;

    cc = texture2D(input_image, pixel_shift_2.zw).rgb;
    factor = weight(cc-c) * kernels[6];
    Z += factor;
    final_colour += factor*cc;

    cc = texture2D(input_image, pixel_shift_3.zw).rgb;
    factor = weight(cc-c) * kernels[7];
    Z += factor;
    final_colour += factor*cc;

    cc = texture2D(input_image, pixel_shift_4.zw).rgb;
    factor = weight(cc-c) * kernels[8];
    Z += factor;
    final_colour += factor*cc;
		
	gl_FragColor = vec4(final_colour/Z, a);
}
#END_CODE