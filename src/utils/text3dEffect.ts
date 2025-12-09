export interface ElementConfig {
    selector: string;
    fontFamily?: string;
    letterSpacing?: string;
}

export interface CharSpanOptions {
    depth?: number;
    colorStart?: number;
    colorStep?: number;
}

export const AnimationState = {
    IDLE: 'idle',
    HOVER: 'hover'
} as const;
export type AnimationState = typeof AnimationState[keyof typeof AnimationState];

export class Text3dEffect {
    private element: HTMLElement | null = null;
    private spans: HTMLElement[] = [];
    private resizeObserver: ResizeObserver | null = null;
    private config: ElementConfig;
    // private options: CharSpanOptions;

    // Lerp variables
    private targetX: number = 0;
    private targetY: number = 0;
    private currentX: number = 0;
    private currentY: number = 0;
    private animationId: number | null = null;
    
    // Hover state
    private hoveredIndex: number = -1;

    constructor(config: ElementConfig, _options: CharSpanOptions = {}) {
        this.config = {
            fontFamily: "'Luckiest Guy', cursive, sans-serif",
            letterSpacing: '-10px',
            ...config
        };
        // this.options = {
        //    depth: 15,
        //    ...options
        // };
    }

    public init(): void {
        this.element = document.querySelector(this.config.selector);
        if (!this.element) {
            console.warn(`Element not found: ${this.config.selector}`);
            return;
        }

        this.processText();
        this.applyParentStyles();
        this.injectGlobalStyles();
        this.bindEvents();
        this.startAnimationLoop();
        // Entrance animation is now triggered manually via playEntrance()
    }

    public playEntrance(): void {
        this.triggerEntranceAnimation();
    }

    private processText(): void {
        if (!this.element) return;

        const text = this.element.textContent || '';
        this.element.innerHTML = ''; // Clear content

        const chars = text.split('');
        
        chars.forEach((char, index) => {
            const span = document.createElement('span');
            
            // Handle whitespace
            if (char === ' ') {
                span.innerHTML = '&nbsp;';
            } else if (char === '\n') {
                span.innerHTML = '<br>';
            } else {
                span.textContent = char;
            }

            this.applySpanStyles(span, index);
            
            // Add hover listeners
            span.addEventListener('mouseenter', () => {
                this.hoveredIndex = index;
            });
            span.addEventListener('mouseleave', () => {
                this.hoveredIndex = -1;
            });

            this.element!.appendChild(span);
            this.spans.push(span);
        });
    }

    private applyParentStyles(): void {
        if (!this.element) return;
        
        const s = this.element.style;
        s.fontFamily = this.config.fontFamily!;
        s.letterSpacing = this.config.letterSpacing!;
        s.display = 'flex';
        s.justifyContent = 'center';
        s.transformStyle = 'preserve-3d';
        s.whiteSpace = 'pre-wrap';
        s.flexWrap = 'wrap';
        s.fontSize = 'clamp(4rem, 8vw, 8rem)';
        s.background = 'none';
        (s as any).webkitTextFillColor = 'initial';
        s.color = 'transparent';
        s.backgroundImage = 'none';
        s.willChange = 'transform, opacity';
        (s as any).contain = 'layout';
    }

    private applySpanStyles(span: HTMLElement, index: number): void {
        const s = span.style;
        
        s.display = 'inline-block';
        s.position = 'relative';
        s.textShadow = '10px 0 10px rgba(0,0,0,0.8), 15px 0 15px rgba(0,0,0,0.5)';
        
        // const totalChars = this.spans.length || 15;
        s.zIndex = `${100 - index}`;
        
        // Initial Transform
        s.transform = 'perspective(500px) rotateY(-15deg) translateZ(0)';
        
        const step = 10; 
        const startHue = 180;
        const hue = startHue + (index * step);
        
        s.color = `hsl(${hue}, 100%, 60%)`;
        
        // Prepare for entrance animation
        s.opacity = '0';
        s.transform = 'perspective(500px) rotateY(-15deg) translateZ(-50px) translateY(20px)';
        this.applyPrefix(span, 'transform', 'perspective(500px) rotateY(-15deg) translateZ(-50px) translateY(20px)');
    }

    private triggerEntranceAnimation(): void {
        this.spans.forEach((span, index) => {
            // Staggered delay
            const delay = index * 50; // 50ms per letter
            
            setTimeout(() => {
                span.style.transition = 'all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)';
                span.style.opacity = '1';
                span.style.transform = 'perspective(500px) rotateY(-15deg) translateZ(0) translateY(0)';
                this.applyPrefix(span, 'transform', 'perspective(500px) rotateY(-15deg) translateZ(0) translateY(0)');
                
                // Remove transition after animation to allow smooth mouse interaction
                setTimeout(() => {
                    span.style.transition = ''; // Clear transition for immediate JS updates
                }, 300);
            }, delay);
        });
    }

    private applyPrefix(el: HTMLElement, prop: string, value: string): void {
        const style = el.style as any;
        const CapProp = prop.charAt(0).toUpperCase() + prop.slice(1);
        style[`webkit${CapProp}`] = value;
        style[`moz${CapProp}`] = value;
        style[prop] = value;
    }

    private injectGlobalStyles(): void {
        const styleId = 'text-3d-effect-styles';
        if (!document.getElementById(styleId)) {
            const style = document.createElement('style');
            style.id = styleId;
            style.textContent = `
                @import url('https://fonts.googleapis.com/css2?family=Luckiest+Guy&display=swap');

                @media (max-width: 768px) {
                    ${this.config.selector} {
                        letter-spacing: -5px !important;
                    }
                    ${this.config.selector} span {
                        transform: perspective(300px) rotateY(-15deg) translateZ(0) !important;
                    }
                }
            `;
            document.head.appendChild(style);
        }
    }

    private bindEvents(): void {
        if (!this.element) return;

        this.element.addEventListener('mousemove', (e) => {
            this.handleMouseMove(e);
        });
        
        this.element.addEventListener('mouseleave', () => {
            this.targetX = 0;
            this.targetY = 0;
        });
        
        let timeout: any;
        this.resizeObserver = new ResizeObserver(() => {
            if (timeout) clearTimeout(timeout);
            timeout = setTimeout(() => {
                this.handleResize();
            }, 100);
        });
        this.resizeObserver.observe(this.element);
    }

    private handleMouseMove(e: MouseEvent): void {
        if (!this.element) return;
        
        const rect = this.element.getBoundingClientRect();
        const centerX = rect.left + rect.width / 2;
        const centerY = rect.top + rect.height / 2;
        
        // Calculate normalized position (-1 to 1)
        this.targetX = (e.clientX - centerX) / (rect.width / 2);
        this.targetY = (e.clientY - centerY) / (rect.height / 2);
    }

    private startAnimationLoop(): void {
        const animate = () => {
            if (!this.element) return;

            // Linear Interpolation (Lerp) for smoothness
            // factor 0.1 means it moves 10% of the distance to target per frame
            const lerpFactor = 0.1;
            
            this.currentX += (this.targetX - this.currentX) * lerpFactor;
            this.currentY += (this.targetY - this.currentY) * lerpFactor;

            // Only update DOM if there's significant change to save performance
            if (Math.abs(this.targetX - this.currentX) > 0.001 || Math.abs(this.targetY - this.currentY) > 0.001) {
                this.updateSpans();
            }

            this.animationId = requestAnimationFrame(animate);
        };
        this.animationId = requestAnimationFrame(animate);
    }

    private updateSpans(): void {
        this.spans.forEach((span, index) => {
            // Stagger factor
            const factor = 1 + (index * 0.05);
            
            const rotateY = -15 + (this.currentX * 20 * factor);
            const rotateX = -(this.currentY * 20 * factor);
            let translateZ = Math.abs(this.currentX * 30); // Slightly reduced pop-out
            let scale = 1;

            // Hover effect
            if (index === this.hoveredIndex) {
                scale = 1.3;
                translateZ += 20; // Pop out more
            }
            
            const transform = `perspective(500px) rotateY(${rotateY}deg) rotateX(${rotateX}deg) translateZ(${translateZ}px) scale(${scale})`;
            
            span.style.transform = transform;
            this.applyPrefix(span, 'transform', transform);
        });
    }

    private handleResize(): void {
        const isMobile = window.innerWidth <= 768;
        this.spans.forEach(span => {
             const persp = isMobile ? '300px' : '500px';
             span.style.transform = `perspective(${persp}) rotateY(-15deg) translateZ(0)`;
             this.applyPrefix(span, 'transform', `perspective(${persp}) rotateY(-15deg) translateZ(0)`);
        });
    }

    public destroy(): void {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect();
        }
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
    }
}

export function useText3dEffect(selector: string, defaultConfig: Partial<ElementConfig> = {}) {
    let effect: Text3dEffect | null = null;

    const init = (config?: Partial<ElementConfig>) => {
        if (effect) effect.destroy();
        effect = new Text3dEffect({ selector, ...defaultConfig, ...config });
        effect.init();
    };

    const playEntrance = () => {
        if (effect) {
            effect.playEntrance();
        }
    };

    const destroy = () => {
        if (effect) effect.destroy();
        effect = null;
    };

    return { init, destroy, playEntrance };
}
