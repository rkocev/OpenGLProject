attribute vec4 a_Position;
uniform float u_Size;
void main() {
    gl_PointSize = u_Size;
    gl_Position = a_Position;
}

