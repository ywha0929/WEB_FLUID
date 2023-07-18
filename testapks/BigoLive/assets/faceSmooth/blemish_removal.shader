#VERTEX_CODE
precision highp float;

attribute vec4 vert_coord;
attribute vec4 text_coord;

varying mediump vec2 frag_text_coord;

void main()
{
    gl_Position = vert_coord;
    frag_text_coord = text_coord.xy;
}
#END_CODE

#FRAGMENT_CODE
precision mediump float;

varying mediump vec2 frag_text_coord;

uniform sampler2D input_image;
uniform sampler2D blur_image;
uniform sampler2D highpass_image;
uniform sampler2D morphological_image;
uniform sampler2D facemask_image;

uniform float min_diff;
uniform float remove_ratio;
uniform bool has_face;

void main()
{
    vec3 input_color = texture2D(input_image, frag_text_coord).rgb;
    vec3 blur_color  = texture2D(blur_image, frag_text_coord).rgb;
    float highpass_value  = texture2D(highpass_image, frag_text_coord).r;
    float morph_highpass_value = texture2D(morphological_image, frag_text_coord).r;
    vec3 facemask_color = texture2D(facemask_image, frag_text_coord).rgb;

    vec3 diff_color = input_color - blur_color;

    diff_color = diff_color * diff_color * 50.0;
    diff_color = min(diff_color, vec3(1.0));

    float input_brightness = (input_color.r + input_color.g + input_color.b) / 3.0;
    float blur_brightness = (blur_color.r + blur_color.g + blur_color.b) / 3.0;

    vec3 result_color = input_color;

    float orig_smaller = 0.0;
    if (has_face) {
        orig_smaller = step(0.5, facemask_color.r) *
                        step(input_brightness, blur_brightness) *
                        step(morph_highpass_value + min_diff, highpass_value);
    } else {
        orig_smaller = step(input_brightness, blur_brightness) *
                        step(morph_highpass_value + min_diff, highpass_value);
    }
    result_color = input_color + orig_smaller * remove_ratio * (blur_color - input_color);
    gl_FragColor = vec4(result_color, 1.0);

}
#END_CODE