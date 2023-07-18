#VERTEX_CODE

precision highp float;

attribute vec4 vert_position;
attribute vec4 text_coord;
varying   vec2 mask_coord;

uniform float pixel_width_offset;
uniform float pixel_height_offset;
varying vec4  pixel_shift_1;
varying vec4  pixel_shift_2;
varying vec4  pixel_shift_3;

void main() {

    gl_Position = vert_position;
    mask_coord  = text_coord.xy;

    vec2 img_coord = vert_position.xy * 0.5 + 0.5;
    pixel_shift_1.xy = img_coord + vec2( pixel_width_offset, -pixel_height_offset);
    pixel_shift_1.zw = img_coord + vec2( pixel_width_offset,  0.0);
    pixel_shift_2.xy = img_coord + vec2( pixel_width_offset,  pixel_height_offset);
    pixel_shift_2.zw = img_coord + vec2(-pixel_width_offset, -pixel_height_offset);
    pixel_shift_3.xy = img_coord + vec2(-pixel_width_offset,  0.0);
    pixel_shift_3.zw = img_coord + vec2(-pixel_width_offset,  pixel_height_offset);
}

#END_CODE

#FRAGMENT_CODE

precision mediump float;

uniform sampler2D face_mask;
uniform sampler2D input_image;

uniform float threshold_min;
uniform float threshold_max;

varying vec2 mask_coord;
varying vec4 pixel_shift_1;
varying vec4 pixel_shift_2;
varying vec4 pixel_shift_3;

void main()
{
    vec4 facemask = texture2D(face_mask, mask_coord);

    float bangs_mask = 0.0;
    if (facemask.a > 0.001)
    {
        vec3 sum = texture2D(input_image, pixel_shift_1.xy).rgb;
        sum += texture2D(input_image, pixel_shift_1.zw).rgb;
        sum += texture2D(input_image, pixel_shift_2.xy).rgb;
        sum -= texture2D(input_image, pixel_shift_2.zw).rgb;
        sum -= texture2D(input_image, pixel_shift_3.xy).rgb;
        sum -= texture2D(input_image, pixel_shift_3.zw).rgb;
        sum = abs(sum);
        bangs_mask = (sum.r + sum.g + sum.b) / 3.0;
        bangs_mask = smoothstep(threshold_min, threshold_max, bangs_mask) * facemask.a;
    }

    gl_FragColor = vec4(facemask.rgb, bangs_mask);
}

#END_CODE