#VERTEX_CODE

precision highp float;

attribute vec4 vert_position;
attribute vec4 text_coord;

varying vec2 output_text_coord;

void main(void) {
    gl_Position = vert_position;
    output_text_coord = text_coord.xy;
}

#END_CODE

#FRAGMENT_CODE

precision mediump float;

uniform sampler2D input_image;

varying vec2 output_text_coord;

void main() {
    vec3 src_color = texture2D(input_image, output_text_coord).rgb;

    float skin_prob = step(0.3745, src_color.r) * step(0.1588, src_color.g) * step(0.0804, src_color.b) * step(0.0608, src_color.r - src_color.g) * step(0.0020, src_color.r - src_color.b);

    gl_FragColor = vec4(vec3(skin_prob), 1.0);
}

#END_CODE