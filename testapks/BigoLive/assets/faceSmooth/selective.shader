#VERTEX_CODE
precision mediump float;

attribute vec2 text_coord;

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
uniform mediump float max_distance;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;
varying mediump vec4 pixel_shift_3;
varying mediump vec4 pixel_shift_4;

void main()
{
    mediump vec3 center_color = texture2D(input_image, output_text_coord).rgb;
    mediump vec3 sum = center_color;
    mediump float wt = 5.0;

    sum += texture2D(input_image, pixel_shift_1.xy).rgb;
    sum += texture2D(input_image, pixel_shift_1.zw).rgb;
    sum += texture2D(input_image, pixel_shift_2.xy).rgb;
    sum += texture2D(input_image, pixel_shift_2.zw).rgb;
    
    mediump vec3 current_color = texture2D(input_image, pixel_shift_3.xy).rgb;
    mediump vec3 color_diff = abs(current_color - center_color);
    mediump float inclusive = step(color_diff.r + color_diff.g + color_diff.b, max_distance);
    sum += current_color * inclusive;
    wt += inclusive;
    
    current_color = texture2D(input_image, pixel_shift_3.zw).rgb;
    color_diff = abs(current_color - center_color);
    inclusive = step(color_diff.r + color_diff.g + color_diff.b, max_distance);
    sum += current_color * inclusive;
    wt += inclusive;
    
    current_color = texture2D(input_image, pixel_shift_4.xy).rgb;
    color_diff = abs(current_color - center_color);
    inclusive = step(color_diff.r + color_diff.g + color_diff.b, max_distance);
    sum += current_color * inclusive;
    wt += inclusive;
    
    current_color = texture2D(input_image, pixel_shift_4.zw).rgb;
    color_diff = abs(current_color - center_color);
    inclusive = step(color_diff.r + color_diff.g + color_diff.b, max_distance);
    sum += current_color * inclusive;
    wt += inclusive;

    gl_FragColor = vec4(sum / wt, 1.0);
}
#END_CODE