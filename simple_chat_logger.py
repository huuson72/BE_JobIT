import tkinter as tk
from tkinter import scrolledtext
import json
from datetime import datetime
import os

class SimpleChatLogger:
    def __init__(self):
        self.window = tk.Tk()
        self.window.title("Simple Chat Logger")
        self.window.geometry("600x400")
        
        # Tạo thư mục logs nếu chưa có
        self.log_dir = "chat_logs"
        if not os.path.exists(self.log_dir):
            os.makedirs(self.log_dir)
        
        # Tạo giao diện
        self.create_widgets()
        
    def create_widgets(self):
        # Khung nhập chat
        self.input_frame = tk.Frame(self.window)
        self.input_frame.pack(fill=tk.X, padx=5, pady=5)
        
        self.input_label = tk.Label(self.input_frame, text="Nhập chat:")
        self.input_label.pack(side=tk.LEFT)
        
        self.input_text = tk.Text(self.input_frame, height=3)
        self.input_text.pack(fill=tk.X, expand=True, padx=5)
        
        # Khung hiển thị chat
        self.chat_display = scrolledtext.ScrolledText(self.window, wrap=tk.WORD, height=15)
        self.chat_display.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # Nút lưu
        self.save_button = tk.Button(self.window, text="Lưu Chat", command=self.save_chat)
        self.save_button.pack(pady=5)
        
        # Label trạng thái
        self.status_label = tk.Label(self.window, text="")
        self.status_label.pack(pady=5)
        
    def save_chat(self):
        # Lấy nội dung từ khung nhập
        chat_content = self.input_text.get("1.0", tk.END).strip()
        if not chat_content:
            self.status_label.config(text="Chưa có nội dung để lưu!")
            return
            
        # Tạo tên file với timestamp
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"chat_{timestamp}.txt"
        filepath = os.path.join(self.log_dir, filename)
        
        # Lưu vào file
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}]\n")
            f.write(chat_content)
            
        # Hiển thị trong khung chat
        self.chat_display.insert(tk.END, f"\n[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}]\n")
        self.chat_display.insert(tk.END, chat_content + "\n")
        self.chat_display.see(tk.END)
        
        # Xóa nội dung đã nhập
        self.input_text.delete("1.0", tk.END)
        
        # Cập nhật trạng thái
        self.status_label.config(text=f"Đã lưu chat vào file: {filename}")
        
    def run(self):
        self.window.mainloop()

if __name__ == "__main__":
    app = SimpleChatLogger()
    app.run() 