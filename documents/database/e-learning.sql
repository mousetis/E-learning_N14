-- 1. Tạo Enums (Kiểu dữ liệu tự định nghĩa)
CREATE TYPE user_role AS ENUM ('STUDENT', 'TEACHER');
CREATE TYPE enrollment_status AS ENUM ('ON_GOING', 'COMPLETED');

-- 2. Tạo các bảng (Tables)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    role user_role DEFAULT 'STUDENT',
    job_title VARCHAR(150),
    bio TEXT,
    location VARCHAR(100),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    icon_url TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE topics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    category_id UUID NOT NULL
);

CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    thumbnail_url TEXT,
    teacher_id UUID NOT NULL,
    category_id UUID NOT NULL,
    price DECIMAL NOT NULL DEFAULT 0,
    discount_price DECIMAL,
    total_lessons INTEGER DEFAULT 0,
    benefits JSONB,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE sections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE lessons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    section_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    video_url TEXT,
    duration_minutes INTEGER,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    course_id UUID NOT NULL,
    progress_percentage DECIMAL DEFAULT 0,
    status enrollment_status DEFAULT 'ON_GOING',
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE (user_id, course_id)
);

CREATE TABLE saved_courses (
    user_id UUID NOT NULL,
    course_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (user_id, course_id)
);

CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL,
    user_id UUID NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE qna (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL,
    user_id UUID NOT NULL,
    parent_id UUID,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL,
    user_id UUID NOT NULL,
    description TEXT,
    thumbnail_url TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE project_resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    file_size INTEGER,
    file_url TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- 3. Thiết lập Khóa ngoại (Foreign Keys)
ALTER TABLE topics ADD FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE;

ALTER TABLE courses ADD FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE RESTRICT;
ALTER TABLE courses ADD FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT;

ALTER TABLE sections ADD FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
ALTER TABLE lessons ADD FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE;

ALTER TABLE enrollments ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE enrollments ADD FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

ALTER TABLE saved_courses ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE saved_courses ADD FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

ALTER TABLE reviews ADD FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
ALTER TABLE reviews ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE qna ADD FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
ALTER TABLE qna ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE qna ADD FOREIGN KEY (parent_id) REFERENCES qna(id) ON DELETE CASCADE;

ALTER TABLE projects ADD FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
ALTER TABLE projects ADD FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE project_resources ADD FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;