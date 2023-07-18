#VERTEX_CODE

precision highp float;

attribute vec4 text_coord;

uniform float pixel_width_offset;
uniform float pixel_height_offset;

varying vec2 output_text_coord;

varying vec4 pixel_shift_1;
varying vec4 pixel_shift_2;
varying vec4 pixel_shift_3;
varying vec4 pixel_shift_4;

void main()
{
    gl_Position = vec4(text_coord.x * 2.0 - 1.0, text_coord.y * 2.0 - 1.0, 0.0, 1.0);

    output_text_coord = text_coord.xy;

    //----------------------------------------------------
    //NOTE: compute pixel coord shift at vertex shader
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

varying highp vec2 output_text_coord;

varying highp vec4 pixel_shift_1;
varying highp vec4 pixel_shift_2;
varying highp vec4 pixel_shift_3;
varying highp vec4 pixel_shift_4;

void main()
{
    vec3 sum = texture2D(input_image, output_text_coord).rgb;

    sum += texture2D(input_image, pixel_shift_1.xy).rgb;
    sum += texture2D(input_image, pixel_shift_1.zw).rgb;
    sum += texture2D(input_image, pixel_shift_2.xy).rgb;
    sum += texture2D(input_image, pixel_shift_2.zw).rgb;
    sum += texture2D(input_image, pixel_shift_3.xy).rgb;
    sum += texture2D(input_image, pixel_shift_3.zw).rgb;
    sum += texture2D(input_image, pixel_shift_4.xy).rgb;
    sum += texture2D(input_image, pixel_shift_4.zw).rgb;

    gl_FragColor = vec4(sum * 0.11111, 1.0);
}

#END_CODE