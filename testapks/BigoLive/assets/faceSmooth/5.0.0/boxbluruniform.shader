#VERTEX_CODE
precision mediump float;

attribute vec2 text_coord;

uniform vec2 pixel_base_offset;
uniform vec2 pixel_stride;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;

void main()
{
    gl_Position = vec4(text_coord.x * 2.0 - 1.0, text_coord.y * 2.0 - 1.0, 0.0, 1.0);

    output_text_coord = text_coord.xy;

    //----------------------------------------------------
    //NOTE: compute pixel coord shift at vertex shader
    pixel_shift_1 = vec4(text_coord.xy - pixel_base_offset - 0.0 * pixel_stride, text_coord.xy + pixel_base_offset + 0.0 * pixel_stride);
    pixel_shift_2 = vec4(text_coord.xy - pixel_base_offset - 1.0 * pixel_stride, text_coord.xy + pixel_base_offset + 1.0 * pixel_stride);

}
#END_CODE

#FRAGMENT_CODE
precision mediump float;

uniform sampler2D input_image;
uniform int dim;

varying mediump vec2 output_text_coord;

varying mediump vec4 pixel_shift_1;
varying mediump vec4 pixel_shift_2;
varying mediump vec4 pixel_shift_3;

void main()
{
    // For strict boxfilter definition, each sampled value except the center one is averaged from the 2 neighboring pixels.
    // It means other pixels are weighted by 0.5.
    // Therefore the center pixel should be weighted by 0.5 also.
    if (dim == 2)
    {
        vec2 sum = texture2D(input_image, output_text_coord).rg * 0.5;

        sum += texture2D(input_image, pixel_shift_1.xy).rg;
        sum += texture2D(input_image, pixel_shift_1.zw).rg;
        sum += texture2D(input_image, pixel_shift_2.xy).rg;
        sum += texture2D(input_image, pixel_shift_2.zw).rg;

        gl_FragColor = vec4(sum * 0.2222, 0.0, 1.0);  // 1/4.5
    }
    else
    {
        vec3 sum = texture2D(input_image, output_text_coord).rgb * 0.5;

        sum += texture2D(input_image, pixel_shift_1.xy).rgb;
        sum += texture2D(input_image, pixel_shift_1.zw).rgb;
        sum += texture2D(input_image, pixel_shift_2.xy).rgb;
        sum += texture2D(input_image, pixel_shift_2.zw).rgb;

        gl_FragColor = vec4(sum * 0.2222, 1.0);  // 1/4.5
    }
}
#END_CODE