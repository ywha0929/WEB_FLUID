#VERTEX_CODE
precision highp float;

attribute vec4 text_coord;

uniform float pixel_width_offset;
uniform float pixel_height_offset;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;

void main()
{
    gl_Position = vec4(text_coord.x * 2.0 - 1.0, text_coord.y * 2.0 - 1.0, 0.0, 1.0);

    output_text_coord = text_coord.xy;

    //----------------------------------------------------
    //NOTE: compute pixel coord shift at vertex shader
    vec2 pixel_step_offset = vec2(pixel_width_offset, pixel_height_offset);

    pixel_shift_1 = vec4(text_coord.xy - 1.0 * pixel_step_offset, text_coord.xy + 1.0 * pixel_step_offset);
    pixel_shift_2 = vec4(text_coord.xy - 2.0 * pixel_step_offset, text_coord.xy + 2.0 * pixel_step_offset);
}
#END_CODE

#FRAGMENT_CODE
precision mediump float;

uniform sampler2D input_image;
uniform int dim;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;

void main()
{
    vec3 input_color = texture2D(input_image, output_text_coord).rgb;
    if (dim == 2) {
        vec2 sum = input_color.rg;

        sum += texture2D(input_image, pixel_shift_1.xy).rg;
        sum += texture2D(input_image, pixel_shift_1.zw).rg;
        sum += texture2D(input_image, pixel_shift_2.xy).rg;
        sum += texture2D(input_image, pixel_shift_2.zw).rg;

        gl_FragColor = vec4(sum * 0.2, input_color.b, 1.0);
    } else {
        vec3 sum = input_color;

        sum += texture2D(input_image, pixel_shift_1.xy).rgb;
        sum += texture2D(input_image, pixel_shift_1.zw).rgb;
        sum += texture2D(input_image, pixel_shift_2.xy).rgb;
        sum += texture2D(input_image, pixel_shift_2.zw).rgb;

        gl_FragColor = vec4(sum * 0.2, 1.0);
    }
}
#END_CODE