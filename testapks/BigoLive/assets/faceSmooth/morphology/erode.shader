#VERTEX_CODE
precision highp float;

attribute vec4 text_coord;

uniform float pixel_width_offset;
uniform float pixel_height_offset;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;
varying mediump vec4 pixel_shift_3;
varying mediump vec4 pixel_shift_4;

void main()
{
    gl_Position = vec4(text_coord.x * 2.0 - 1.0, text_coord.y * 2.0 - 1.0, 0.0, 1.0);

    output_text_coord = text_coord.xy;

    //----------------------------------------------------
    //NOTE: compute pixel coord shift at vertex shader

    pixel_shift_1 = vec4(text_coord.x - pixel_width_offset, text_coord.y,        text_coord.x + pixel_width_offset, text_coord.y);
    pixel_shift_2 = vec4(text_coord.x, text_coord.y - pixel_height_offset,       text_coord.x, text_coord.y + pixel_height_offset);
    pixel_shift_3 = vec4(text_coord.x - 2.0 * pixel_width_offset, text_coord.y,  text_coord.x + 2.0 * pixel_width_offset, text_coord.y);
    pixel_shift_4 = vec4(text_coord.x, text_coord.y - 2.0 * pixel_height_offset, text_coord.x, text_coord.y + 2.0 * pixel_height_offset);
}
#END_CODE

#FRAGMENT_CODE
precision mediump float;

uniform sampler2D input_image;

varying vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;
varying mediump vec4 pixel_shift_3;
varying mediump vec4 pixel_shift_4;

void main()
{
    vec4 color = texture2D(input_image, output_text_coord).rgba;
    vec2 cur_color1 = texture2D(input_image, pixel_shift_1.xy).rg;
    vec2 cur_color2 = texture2D(input_image, pixel_shift_1.zw).rg;
    vec2 cur_color3 = texture2D(input_image, pixel_shift_2.xy).rg;
    vec2 cur_color4 = texture2D(input_image, pixel_shift_2.zw).rg;
    cur_color1 = min(cur_color1, cur_color2);
    cur_color3 = min(cur_color3, cur_color4);
    cur_color1 = min(cur_color1, cur_color3);
    color.rg = min(color.rg, cur_color1);

    cur_color1 = texture2D(input_image, pixel_shift_3.xy).rg;
    cur_color2 = texture2D(input_image, pixel_shift_3.zw).rg;
    cur_color3 = texture2D(input_image, pixel_shift_4.xy).rg;
    cur_color4 = texture2D(input_image, pixel_shift_4.zw).rg;
    cur_color1 = min(cur_color1, cur_color2);
    cur_color3 = min(cur_color3, cur_color4);
    cur_color1 = min(cur_color1, cur_color3);
    color.rg = min(color.rg, cur_color1);

    gl_FragColor = color;
}
#END_CODE