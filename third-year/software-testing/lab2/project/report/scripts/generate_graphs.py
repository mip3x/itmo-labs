#!/usr/bin/env python3
import csv
import math
import sys
from pathlib import Path


def read_points(path):
    points = []
    with open(path) as file:
        for row in csv.DictReader(file):
            points.append((float(row["X"]), float(row["Result"])))
    return points


def screen_x(x, x_min, x_max):
    return 80 + (x - x_min) / (x_max - x_min) * 990


def screen_y(y, y_min, y_max):
    return 400 - (y - y_min) / (y_max - y_min) * 360


def split_segments(points, y_min, y_max):
    segments = []
    current = []

    for x, y in points:
        if math.isnan(y) or math.isinf(y) or y < y_min or y > y_max:
            if len(current) > 1:
                segments.append(current)
            current = []
            continue

        current.append((x, y))

    if len(current) > 1:
        segments.append(current)

    return segments


def nearest_point(points, marker_x):
    return min(points, key=lambda point: abs(point[0] - marker_x))


def asymptote_label(value):
    variants = {
        -2 * math.pi: "-2pi",
        -3 * math.pi / 2: "-3pi/2",
        -math.pi: "-pi",
        -math.pi / 2: "-pi/2",
        0.0: "0",
        math.pi / 2: "pi/2",
        math.pi: "pi",
        3 * math.pi / 2: "3pi/2",
        2 * math.pi: "2pi",
    }

    for key, label in variants.items():
        if abs(value - key) < 1e-6:
            return label

    return f"{value:.2f}"


def main():
    if len(sys.argv) < 6:
        print("Usage: python3 report/scripts/generate_graphs.py <input.csv> <output.svg> <title> <y_min> <y_max> [a:<x> ...] [p:<x> ...]")
        sys.exit(1)

    input_path = Path(sys.argv[1])
    output_path = Path(sys.argv[2])
    title = sys.argv[3]
    y_min = float(sys.argv[4])
    y_max = float(sys.argv[5])
    asymptotes = []
    points_to_mark = []
    for value in sys.argv[6:]:
        if value.startswith("a:"):
            asymptotes.append(float(value[2:]))
        elif value.startswith("p:"):
            points_to_mark.append(float(value[2:]))
        else:
            points_to_mark.append(float(value))

    points = read_points(input_path)
    x_min = min(x for x, _ in points)
    x_max = max(x for x, _ in points)
    segments = split_segments(points, y_min, y_max)

    svg = [
        '<svg xmlns="http://www.w3.org/2000/svg" width="1100" height="520" viewBox="0 0 1100 520">',
        '<rect width="100%" height="100%" fill="#fcfbf7"/>',
        '<rect x="20" y="20" width="1060" height="480" rx="18" fill="#fffef8" stroke="#d6cfbf" stroke-width="1.5"/>',
        f'<text x="550" y="28" text-anchor="middle" font-family="JetBrains Mono" font-size="20" fill="#3a3127">{title}</text>',
        '<rect x="80" y="40" width="990" height="360" fill="#fffdfa" stroke="#e5dece" stroke-width="1"/>',
    ]

    for i in range(7):
        y = y_min + (y_max - y_min) * i / 6
        y_pos = screen_y(y, y_min, y_max)
        svg.append(f'<line x1="80" y1="{y_pos:.2f}" x2="1070" y2="{y_pos:.2f}" stroke="#ebe4d6" stroke-width="1"/>')
        svg.append(f'<text x="70" y="{y_pos + 4:.2f}" text-anchor="end" font-family="JetBrains Mono" font-size="11" fill="#6d6254">{y:.2f}</text>')

    for i in range(9):
        x = x_min + (x_max - x_min) * i / 8
        x_pos = screen_x(x, x_min, x_max)
        svg.append(f'<line x1="{x_pos:.2f}" y1="40" x2="{x_pos:.2f}" y2="400" stroke="#ebe4d6" stroke-width="1"/>')
        svg.append(f'<text x="{x_pos:.2f}" y="420" text-anchor="middle" font-family="JetBrains Mono" font-size="11" fill="#6d6254">{x:.2f}</text>')

    if x_min <= 0.0 <= x_max:
        x_zero = screen_x(0.0, x_min, x_max)
        svg.append(f'<line x1="{x_zero:.2f}" y1="40" x2="{x_zero:.2f}" y2="400" stroke="#615548" stroke-width="1.5"/>')
    if y_min <= 0.0 <= y_max:
        y_zero = screen_y(0.0, y_min, y_max)
        svg.append(f'<line x1="80" y1="{y_zero:.2f}" x2="1070" y2="{y_zero:.2f}" stroke="#615548" stroke-width="1.5"/>')

    for index, asymptote in enumerate(asymptotes):
        if not x_min <= asymptote <= x_max:
            continue

        ax = screen_x(asymptote, x_min, x_max)
        label_y = 58 + (index % 3) * 14
        label = asymptote_label(asymptote)
        svg.append(f'<line x1="{ax:.2f}" y1="40" x2="{ax:.2f}" y2="400" stroke="#2a6f97" stroke-width="1.4" stroke-dasharray="6 5"/>')
        svg.append(f'<text x="{ax + 4:.2f}" y="{label_y:.2f}" text-anchor="start" font-family="JetBrains Mono" font-size="10" fill="#2a6f97">asymptote x={label} ({asymptote:.2f})</text>')

    for segment in segments:
        polyline = []
        for x, y in segment:
            polyline.append(f"{screen_x(x, x_min, x_max):.2f},{screen_y(y, y_min, y_max):.2f}")
        svg.append(f'<polyline points="{" ".join(polyline)}" fill="none" stroke="#bf5b39" stroke-width="2.2" stroke-linejoin="round" stroke-linecap="round"/>')

    for index, marker in enumerate(points_to_mark):
        if not x_min <= marker <= x_max:
            continue

        point_x, point_y = nearest_point(points, marker)
        point_y = max(y_min, min(y_max, point_y))
        marker_x = screen_x(point_x, x_min, x_max)
        marker_y = screen_y(point_y, y_min, y_max)
        label_dx = 18 if index % 2 == 0 else -18
        label_anchor = "start" if index % 2 == 0 else "end"
        label_y = marker_y - 12 if index % 2 == 0 else marker_y + 22

        svg.append(f'<circle cx="{marker_x:.2f}" cy="{marker_y:.2f}" r="4.5" fill="#2a6f97" stroke="#fffdfa" stroke-width="1.5"/>')
        svg.append(f'<line x1="{marker_x:.2f}" y1="{marker_y:.2f}" x2="{marker_x + label_dx:.2f}" y2="{label_y - 5:.2f}" stroke="#2a6f97" stroke-width="1.2"/>')
        svg.append(f'<text x="{marker_x + label_dx:.2f}" y="{label_y:.2f}" text-anchor="{label_anchor}" font-family="JetBrains Mono" font-size="10" fill="#2a6f97">x={marker:.2f}</text>')

    svg.append('<text x="575" y="508" text-anchor="middle" font-family="JetBrains Mono" font-size="12" fill="#4c4338">x</text>')
    svg.append('<text x="22" y="220" transform="rotate(-90 22 220)" text-anchor="middle" font-family="JetBrains Mono" font-size="12" fill="#4c4338">y</text>')
    svg.append(f'<text x="1066" y="508" text-anchor="end" font-family="JetBrains Mono" font-size="10" fill="#8c7f71">Y clipped to [{y_min:.2f}; {y_max:.2f}]</text>')
    svg.append("</svg>")

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text("\n".join(svg))
    print(f"Generated plot: {output_path}")


if __name__ == "__main__":
    main()
