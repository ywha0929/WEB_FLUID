#VERTEX_CODE
precision mediump float;

attribute vec2 text_coord;

uniform vec2 pixel_base_offset;
uniform vec2 pixel_stride;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;
varying mediump vec4 pixel_shift_3;
varying mediump vec4 pixel_shift_4;
varying mediump vec4 pixel_shift_5;

void main()
{
    gl_Position = vec4(text_coord.x * 2.0 - 1.0, text_coord.y * 2.0 - 1.0, 0.0, 1.0);

    output_text_coord = text_coord.xy;

    //----------------------------------------------------
    //NOTE: compute pixel coord shift at vertex shader
    pixel_shift_1 = vec4(text_coord.xy - pixel_base_offset - 0.0 * pixel_stride, text_coord.xy + pixel_base_offset + 0.0 * pixel_stride);
    pixel_shift_2 = vec4(text_coord.xy - pixel_base_offset - 1.0 * pixel_stride, text_coord.xy + pixel_base_offset + 1.0 * pixel_stride);
    pixel_shift_3 = vec4(text_coord.xy - pixel_base_offset - 2.0 * pixel_stride, text_coord.xy + pixel_base_offset + 2.0 * pixel_stride);
    pixel_shift_4 = vec4(text_coord.xy - pixel_base_offset - 3.0 * pixel_stride, text_coord.xy + pixel_base_offset + 3.0 * pixel_stride);
    pixel_shift_5 = vec4(text_coord.xy - pixel_base_offset - 4.0 * pixel_stride, text_coord.xy + pixel_base_offset + 4.0 * pixel_stride);
}
#END_CODE

#FRAGMENT_CODE
precision mediump float;
uniform sampler2D input_image;
uniform sampler2D facemask_image;
uniform mediump float max_distance;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;
varying mediump vec4 pixel_shift_3;
varying mediump vec4 pixel_shift_4;
varying mediump vec4 pixel_shift_5;

void main()
{
    mediump vec3  center_color;
    mediump vec3  img_color;
    mediump vec3  facemask_color;
    mediump vec3  color_diff;
    mediump vec3  sum;
    mediump float facemask_wt;
    mediump float pixel_wt;
    mediump float wt;

    // For strict boxfilter definition, each sampled value except the center one is averaged from the 2 neighboring pixels.
    // It means other pixels are weighted by 0.5.
    // Therefore the center pixel should be weighted by 0.5 also.

    center_color = texture2D(input_image, output_text_coord).rgb;
    sum          = center_color * 0.5;
    wt           = 0.5;

    facemask_color =  texture2D(facemask_image, pixel_shift_1.xy).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    sum            += texture2D(input_image, pixel_shift_1.xy).rgb * facemask_wt;
    wt             += facemask_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_1.zw).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    sum            += texture2D(input_image, pixel_shift_1.zw).rgb * facemask_wt;
    wt             += facemask_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_2.xy).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    sum            += texture2D(input_image, pixel_shift_2.xy).rgb * facemask_wt;
    wt             += facemask_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_2.zw).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    sum            += texture2D(input_image, pixel_shift_2.zw).rgb * facemask_wt;
    wt             += facemask_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_3.xy).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    img_color      =  texture2D(input_image, pixel_shift_3.xy).rgb;
    color_diff     =  abs(img_color - center_color);
    pixel_wt       =  step(color_diff.r + color_diff.g + color_diff.b, max_distance) * facemask_wt;
    sum            += img_color * pixel_wt;
    wt             += pixel_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_3.zw).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    img_color      =  texture2D(input_image, pixel_shift_3.zw).rgb;
    color_diff     =  abs(img_color - center_color);
    pixel_wt       =  step(color_diff.r + color_diff.g + color_diff.b, max_distance) * facemask_wt;
    sum            += img_color * pixel_wt;
    wt             += pixel_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_4.xy).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    img_color      =  texture2D(input_image, pixel_shift_4.xy).rgb;
    color_diff     =  abs(img_color - center_color);
    pixel_wt       =  step(color_diff.r + color_diff.g + color_diff.b, max_distance) * facemask_wt;
    sum            += img_color * pixel_wt;
    wt             += pixel_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_4.zw).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    img_color      =  texture2D(input_image, pixel_shift_4.zw).rgb;
    color_diff     =  abs(img_color - center_color);
    pixel_wt       =  step(color_diff.r + color_diff.g + color_diff.b, max_distance) * facemask_wt;
    sum            += img_color * pixel_wt;
    wt             += pixel_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_5.xy).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    img_color      =  texture2D(input_image, pixel_shift_5.xy).rgb;
    color_diff     =  abs(img_color - center_color);
    pixel_wt       =  step(color_diff.r + color_diff.g + color_diff.b, max_distance) * facemask_wt;
    sum            += img_color * pixel_wt;
    wt             += pixel_wt;

    facemask_color =  texture2D(facemask_image, pixel_shift_5.zw).rgb;
    facemask_wt    =  max(facemask_color.r, 1.0 - facemask_color.b);
    img_color      =  texture2D(input_image, pixel_shift_5.zw).rgb;
    color_diff     =  abs(img_color - center_color);
    pixel_wt       =  step(color_diff.r + color_diff.g + color_diff.b, max_distance) * facemask_wt;
    sum            += img_color * pixel_wt;
    wt             += pixel_wt;

    gl_FragColor = vec4(sum / wt, 1.0);
}
#END_CODE