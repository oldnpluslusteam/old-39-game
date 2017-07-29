#ifdef GL_ES
    precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec4 textureSample = texture2D(u_texture, v_texCoords);

    float a = 0.5 + 0.5 * sign(textureSample.a - 0.5);

    gl_FragColor = vec4(0.5, 0.5, 1.0, a);
}
